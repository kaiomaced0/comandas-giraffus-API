package k.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import k.dto.PagamentoDTO;
import k.dto.PagamentoDeleteDTO;
import k.dto.PagamentoItemDTO;
import k.dto.PagamentoResponseDTO;
import k.exception.BusinessException;
import k.model.Caixa;
import k.model.Comanda;
import k.model.FormaPagamento;
import k.model.ItemCompra;
import k.model.Pagamento;
import k.model.PagamentoItem;
import k.model.PagamentoRemovidoHistorico;
import k.model.Usuario;
import k.model.enums.ModoPagamento;
import k.repository.CaixaRepository;
import k.repository.ComandaRepository;
import k.repository.ItemCompraRepository;
import k.repository.PagamentoItemRepository;
import k.repository.PagamentoRemovidoHistoricoRepository;
import k.repository.PagamentoRepository;
import k.repository.UsuarioRepository;
import k.service.PagamentoService;
import k.service.UsuarioLogadoService;

@ApplicationScoped
public class PagamentoServiceImpl implements PagamentoService {

    @Inject
    PagamentoRepository repository;

    @Inject
    ComandaRepository comandaRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    CaixaRepository caixaRepository;

    @Inject
    ItemCompraRepository itemCompraRepository;

    @Inject
    PagamentoItemRepository pagamentoItemRepository;

    @Inject
    PagamentoRemovidoHistoricoRepository pagamentoRemovidoHistoricoRepository;

    @Override
    public List<PagamentoResponseDTO> getAll() {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        return repository.findAll().stream()
                .filter(pagamento -> pagamento.getUsuarioCaixa() != null
                        && pagamento.getUsuarioCaixa().getEmpresa() != null
                        && pagamento.getUsuarioCaixa().getEmpresa().getId().equals(u.getEmpresa().getId()))
                .map(PagamentoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public PagamentoResponseDTO getId(Long id) {
        Pagamento entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Pagamento não encontrado");
        }
        return new PagamentoResponseDTO(entity);
    }

    @Override
    public List<PagamentoResponseDTO> getByComanda(Long idComanda) {
        Comanda comanda = comandaRepository.findById(idComanda);
        if (comanda == null) {
            throw new NotFoundException("Comanda não encontrada");
        }
        return repository.find("comanda = ?1", comanda).list().stream()
                .map(PagamentoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Response insert(PagamentoDTO dto) {
        if (dto == null) {
            throw new BusinessException("Payload de pagamento é obrigatório");
        }
        if (dto.idComanda() == null) {
            throw new BusinessException("idComanda é obrigatório");
        }
        if (dto.valorTotal() == null) {
            throw new BusinessException("valorTotal é obrigatório");
        }
        if (dto.valorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("valorTotal deve ser maior que zero");
        }
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Caixa caixaAberto = caixaRepository.findAbertoPorUsuario(u);
        if (caixaAberto == null) {
            throw new BusinessException("Usuário não tem caixa aberto");
        }
        Comanda comanda = comandaRepository.findById(dto.idComanda());
        if (comanda == null) {
            throw new NotFoundException("Comanda não encontrada");
        }
        ModoPagamento modo = dto.idModoPagamento() == null
                ? ModoPagamento.SIMPLES
                : ModoPagamento.valueOf(dto.idModoPagamento());
        BigDecimal precoComanda = comanda.getPreco() == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(comanda.getPreco());

        Pagamento entity = new Pagamento();
        entity.setComanda(comanda);
        entity.setFormaPagamento(dto.idFormaPagamento() == null
                ? null
                : FormaPagamento.valueOf(dto.idFormaPagamento()));
        entity.setModo(modo);
        entity.setCaixa(caixaAberto);
        entity.setUsuarioCaixa(u);
        entity.setValorTotal(dto.valorTotal());
        entity.setValorPagamento(dto.valorTotal());
        entity.setEstornado(false);
        entity.setPagamentoRealizado(true);

        if (modo == ModoPagamento.SIMPLES) {
            BigDecimal jaPago = somaPagamentosValidos(comanda, null);
            BigDecimal novoTotal = jaPago.add(dto.valorTotal());
            if (novoTotal.compareTo(precoComanda) > 0) {
                BigDecimal restante = precoComanda.subtract(jaPago).max(BigDecimal.ZERO);
                BigDecimal gorjeta = novoTotal.subtract(precoComanda);
                entity.setValorGorjeta(gorjeta);
                entity.setValorPagamento(restante);
            } else {
                entity.setValorGorjeta(BigDecimal.ZERO);
                entity.setValorPagamento(dto.valorTotal());
            }
        } else {
            // RATEADO
            if (dto.itens() == null || dto.itens().isEmpty()) {
                throw new BusinessException("Pagamento rateado exige lista de itens");
            }
            validarItensRateados(comanda, dto.itens());
            entity.setValorGorjeta(BigDecimal.ZERO);
        }

        repository.persist(entity);
        entity.setItens(new ArrayList<>());
        if (modo == ModoPagamento.RATEADO) {
            for (PagamentoItemDTO itemDto : dto.itens()) {
                ItemCompra ic = itemCompraRepository.findById(itemDto.itemCompraId());
                if (ic == null) {
                    throw new NotFoundException("ItemCompra " + itemDto.itemCompraId() + " não encontrado");
                }
                PagamentoItem pi = new PagamentoItem();
                pi.setPagamento(entity);
                pi.setItemCompra(ic);
                pi.setQuantidade(itemDto.quantidade());
                pi.setValorAbatido(itemDto.valorAbatido() == null ? BigDecimal.ZERO : itemDto.valorAbatido());
                pagamentoItemRepository.persist(pi);
                entity.getItens().add(pi);
            }
        }

        atualizarFinalizacaoComanda(comanda);
        return Response.ok(new PagamentoResponseDTO(entity)).build();
    }

    @Override
    @Transactional
    public Response estornar(Long id) {
        Pagamento p = repository.findById(id);
        if (p == null) {
            throw new NotFoundException("Pagamento não encontrado");
        }
        if (Boolean.TRUE.equals(p.getEstornado())) {
            throw new BusinessException("Pagamento já está estornado");
        }
        p.setEstornado(true);
        p.setPagamentoRealizado(false);
        atualizarFinalizacaoComanda(p.getComanda());
        return Response.ok(new PagamentoResponseDTO(p)).build();
    }

    @Override
    @Transactional
    public Response delete(PagamentoDeleteDTO dto) {
        Pagamento pagamento = repository.findById(dto.id());
        if (pagamento == null) {
            throw new NotFoundException("Pagamento não encontrado");
        }
        pagamento.setAtivo(false);
        PagamentoRemovidoHistorico pagamentoRemovido = new PagamentoRemovidoHistorico();
        pagamentoRemovido.setComentario(dto.observacao());
        pagamentoRemovido.setPagamento(pagamento);
        pagamentoRemovidoHistoricoRepository.persist(pagamentoRemovido);
        atualizarFinalizacaoComanda(pagamento.getComanda());
        return Response.ok().build();
    }

    private void validarItensRateados(Comanda comanda, List<PagamentoItemDTO> itens) {
        for (PagamentoItemDTO itemDto : itens) {
            if (itemDto.itemCompraId() == null) {
                throw new BusinessException("itemCompraId obrigatório em pagamento rateado");
            }
            if (itemDto.quantidade() == null || itemDto.quantidade() <= 0) {
                throw new BusinessException("quantidade deve ser > 0");
            }
            ItemCompra ic = itemCompraRepository.findById(itemDto.itemCompraId());
            if (ic == null) {
                throw new NotFoundException("ItemCompra " + itemDto.itemCompraId() + " não encontrado");
            }
            int restante = quantidadeRestante(ic);
            if (itemDto.quantidade() > restante) {
                throw new BusinessException(
                        "ItemCompra " + ic.getId() + ": quantidade solicitada (" + itemDto.quantidade()
                                + ") > restante (" + restante + ")");
            }
        }
    }

    private int quantidadeRestante(ItemCompra ic) {
        int total = ic.getQuantidade() == null ? 0 : ic.getQuantidade();
        int pago = pagamentoItemRepository.findByItemCompra(ic).stream()
                .filter(pi -> pi.getPagamento() != null && !Boolean.TRUE.equals(pi.getPagamento().getEstornado()))
                .mapToInt(pi -> pi.getQuantidade() == null ? 0 : pi.getQuantidade())
                .sum();
        return total - pago;
    }

    BigDecimal somaPagamentosValidos(Comanda comanda, Long excludeId) {
        List<Pagamento> pagamentos = repository.find("comanda = ?1 and estornado = false", comanda).list();
        return pagamentos.stream()
                .filter(p -> excludeId == null || !p.getId().equals(excludeId))
                .map(Pagamento::getValorTotal)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void atualizarFinalizacaoComanda(Comanda comanda) {
        if (comanda == null) {
            return;
        }
        BigDecimal soma = somaPagamentosValidos(comanda, null);
        BigDecimal preco = comanda.getPreco() == null ? BigDecimal.ZERO : BigDecimal.valueOf(comanda.getPreco());
        boolean finalizada = soma.setScale(2, RoundingMode.HALF_UP)
                .compareTo(preco.setScale(2, RoundingMode.HALF_UP)) >= 0
                && preco.compareTo(BigDecimal.ZERO) > 0;
        comanda.setFinalizada(finalizada);
    }
}
