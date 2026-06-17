package k.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import k.dto.CaixaResponseDTO;
import k.dto.PagamentoDTO;
import k.dto.PagamentoDeleteDTO;
import k.dto.PagamentoEstornarInputDTO;
import k.dto.PagamentoMultiInputDTO;
import k.dto.PagamentoMultiResponseDTO;
import k.dto.PagamentoResponseDTO;
import k.dto.PagedResponse;
import k.model.*;
import k.repository.CaixaRepository;
import k.repository.ComandaRepository;
import k.repository.ItemCompraRepository;
import k.repository.PagamentoItemRepository;
import k.repository.PagamentoRemovidoHistoricoRepository;
import k.repository.PagamentoRepository;
import k.repository.UsuarioRepository;
import k.service.CaixaService;
import k.service.PagamentoService;
import k.service.UsuarioLogadoService;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PagamentoServiceImpl implements PagamentoService {

    private static final Logger LOG = Logger.getLogger(PagamentoServiceImpl.class);

    // Tolerância para validações de soma decimal (R$ 0,01).
    private static final BigDecimal TOLERANCIA = new BigDecimal("0.01");

    // Cálculo simples de gorjeta (10% do próprio pagamento quando taxaServico=true).
    private static final BigDecimal TAXA_SERVICO = new BigDecimal("0.10");

    @Inject
    PagamentoRepository repository;

    @Inject
    PagamentoItemRepository pagamentoItemRepository;

    @Inject
    ComandaRepository comandaRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    PagamentoRemovidoHistoricoRepository pagamentoRemovidoHistoricoRepository;

    @Inject
    ItemCompraRepository itemCompraRepository;

    @Inject
    CaixaRepository caixaRepository;

    @Inject
    CaixaService caixaService;

    @Override
    public List<PagamentoResponseDTO> getAll() {
        Empresa emp = usuarioLogadoService.getEmpresaLogada();
        return repository.findAll().stream()
                .filter(pagamento -> pagamento.getUsuarioCaixa().getEmpresa() == emp)
                .filter(EntityClass::getAtivo)
                .map(pagamento -> new PagamentoResponseDTO(pagamento.getComanda().getId(),
                        pagamento.getPagamentoRealizado(), pagamento.getFormaPagamento(),
                        pagamento.getUsuarioCaixa().getId(),
                        pagamento.getValorPagamento()))
                .collect(Collectors.toList());
    }

    @Override
    public PagamentoResponseDTO getId(Long id) {
        Pagamento entity = repository.findById(id);
        return new PagamentoResponseDTO(entity.getComanda().getId(), entity.getPagamentoRealizado(),
                entity.getFormaPagamento(),
                entity.getUsuarioCaixa().getId(), entity.getValorPagamento());
    }

    @Override
    @Transactional
    public Response insert(PagamentoDTO pagamentoDTO) {
        Empresa emp = usuarioLogadoService.getEmpresaLogada();
        try {
            if (emp.getCaixaAtual() == null) {
                throw new Exception("Caixa atual não existe!");
            }
            Pagamento entity = new Pagamento();
            entity.setComanda(comandaRepository.findById(pagamentoDTO.idComanda()));
            entity.setFormaPagamento(FormaPagamento.valueOf(pagamentoDTO.idFormaPagamento()));
            entity.setUsuarioCaixa(usuarioRepository.findById(usuarioLogadoService.getPerfilUsuarioLogado().getId()));
            entity.setValorPagamento(pagamentoDTO.valorPagamento());
            if (entity.getValorPagamento() < entity.getComanda().getPreco()) {
                throw new Exception();
            }
            entity.setPagamentoRealizado(true);
            entity.getComanda().setFinalizada(true);
            entity.setValorGorjeta(entity.getValorPagamento() - entity.getComanda().getPreco());
            // Onda F: novos campos preenchidos com defaults para compat
            entity.setModo(ModoPagamento.SIMPLES);
            entity.setEstornado(false);
            entity.setValorTotal(BigDecimal.valueOf(entity.getValorPagamento()));
            repository.persist(entity);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).entity(e).build();
        }

    }

    @Override
    @Transactional
    public Response delete(PagamentoDeleteDTO pagamentoDeleteDTO) {
        Pagamento pagamento = repository.findById(pagamentoDeleteDTO.id());
        pagamento.setAtivo(false);
        PagamentoRemovidoHistorico pagamentoRemovido = new PagamentoRemovidoHistorico();
        pagamentoRemovido.setComentario(pagamentoDeleteDTO.observacao());
        pagamentoRemovidoHistoricoRepository.persist(pagamentoRemovido);
        return Response.ok().build();
    }

    // ===================== Onda F - pagamentos múltiplos =====================

    @Override
    @Transactional
    public PagamentoMultiResponseDTO insert(Long comandaId, PagamentoMultiInputDTO dto) {
        if (dto == null) {
            throw new WebApplicationException("Body obrigatório", Response.Status.BAD_REQUEST);
        }
        if (dto.idFormaPagamento() == null) {
            throw new WebApplicationException("Forma de pagamento obrigatória", Response.Status.BAD_REQUEST);
        }
        if (dto.valorTotal() == null || dto.valorTotal().signum() <= 0) {
            throw new WebApplicationException("valorTotal deve ser positivo", Response.Status.BAD_REQUEST);
        }

        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null) {
            throw new WebApplicationException("Usuário não autenticado", Response.Status.UNAUTHORIZED);
        }

        Comanda comanda = resolverComandaDaEmpresa(comandaId, logado);

        // Caixa aberto do logado (422 se não houver)
        Caixa caixaAberto;
        try {
            CaixaResponseDTO caixaResp = caixaService.meuCaixaAberto();
            caixaAberto = caixaRepository.findById(caixaResp.id());
        } catch (WebApplicationException wae) {
            // Re-mapeia 404 (sem caixa aberto) para 422 conforme escopo da onda
            throw new WebApplicationException("Não há caixa aberto para o usuário logado", 422);
        }
        if (caixaAberto == null) {
            throw new WebApplicationException("Não há caixa aberto para o usuário logado", 422);
        }

        ModoPagamento modo = parseModo(dto.modo());

        BigDecimal preco = comanda.getPreco() == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(comanda.getPreco());
        BigDecimal jaPago = somaPagamentosNaoEstornados(comanda);

        if (modo == ModoPagamento.SIMPLES) {
            BigDecimal novoTotal = jaPago.add(dto.valorTotal());
            if (novoTotal.subtract(preco).compareTo(TOLERANCIA) > 0) {
                throw new WebApplicationException(
                        "Soma dos pagamentos excede o preço da comanda", 422);
            }
        } else { // RATEADO
            if (dto.itens() == null || dto.itens().isEmpty()) {
                throw new WebApplicationException(
                        "Modo RATEADO exige lista de itens", Response.Status.BAD_REQUEST);
            }
            validarRateado(comanda, dto);
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setComanda(comanda);
        pagamento.setModo(modo);
        pagamento.setCaixa(caixaAberto);
        pagamento.setFormaPagamento(FormaPagamento.valueOf(dto.idFormaPagamento()));
        pagamento.setValorTotal(dto.valorTotal());
        pagamento.setValorPagamento(dto.valorTotal().doubleValue());
        pagamento.setUsuarioCaixa(logado);
        pagamento.setPagamentoRealizado(true);
        pagamento.setEstornado(false);

        Boolean taxa = dto.taxaServico();
        BigDecimal gorjeta = Boolean.TRUE.equals(taxa)
                ? dto.valorTotal().multiply(TAXA_SERVICO).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        pagamento.setValorGorjeta(gorjeta.doubleValue());

        repository.persist(pagamento);

        if (modo == ModoPagamento.RATEADO) {
            List<PagamentoItem> itens = new ArrayList<>();
            for (var item : dto.itens()) {
                ItemCompra ic = itemCompraRepository.findById(item.itemCompraId());
                PagamentoItem pi = new PagamentoItem();
                pi.setPagamento(pagamento);
                pi.setItemCompra(ic);
                pi.setQuantidade(item.quantidade());
                pi.setValorAbatido(item.valorAbatido());
                pagamentoItemRepository.persist(pi);
                itens.add(pi);
            }
            pagamento.setItens(itens);
        }

        // Recalcula soma e ajusta finalizada
        BigDecimal somaAtual = somaPagamentosNaoEstornados(comanda);
        if (somaAtual.add(TOLERANCIA).compareTo(preco) >= 0) {
            comanda.setFinalizada(true);
        } else {
            comanda.setFinalizada(false);
        }

        LOG.info("Pagamento criado id=" + pagamento.getId()
                + " comanda=" + comanda.getId()
                + " modo=" + modo
                + " valorTotal=" + dto.valorTotal());

        return new PagamentoMultiResponseDTO(pagamento);
    }

    @Override
    @Transactional
    public PagamentoMultiResponseDTO estornar(Long pagamentoId, PagamentoEstornarInputDTO dto) {
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null) {
            throw new WebApplicationException("Usuário não autenticado", Response.Status.UNAUTHORIZED);
        }
        if (pagamentoId == null) {
            throw new NotFoundException("Pagamento não encontrado");
        }
        Pagamento pagamento = repository.findById(pagamentoId);
        if (pagamento == null || !Boolean.TRUE.equals(pagamento.getAtivo())) {
            throw new NotFoundException("Pagamento não encontrado");
        }
        // Validação de empresa: usuario do caixa do pagamento deve ser da mesma empresa do logado
        if (pagamento.getUsuarioCaixa() == null
                || pagamento.getUsuarioCaixa().getEmpresa() == null
                || logado.getEmpresa() == null
                || !pagamento.getUsuarioCaixa().getEmpresa().getId().equals(logado.getEmpresa().getId())) {
            throw new NotFoundException("Pagamento não encontrado");
        }
        if (Boolean.TRUE.equals(pagamento.getEstornado())) {
            throw new WebApplicationException("Pagamento já estornado", Response.Status.CONFLICT);
        }

        pagamento.setEstornado(true);

        Comanda comanda = pagamento.getComanda();
        if (comanda != null) {
            BigDecimal preco = comanda.getPreco() == null
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(comanda.getPreco());
            BigDecimal somaAtual = somaPagamentosNaoEstornados(comanda);
            if (Boolean.TRUE.equals(comanda.getFinalizada())
                    && somaAtual.add(TOLERANCIA).compareTo(preco) < 0) {
                comanda.setFinalizada(false);
            }
        }

        String motivo = dto == null ? null : dto.motivo();
        LOG.info("Pagamento estornado id=" + pagamento.getId()
                + " por=" + logado.getLogin()
                + " motivo=" + motivo);

        return new PagamentoMultiResponseDTO(pagamento);
    }

    @Override
    public List<PagamentoMultiResponseDTO> listarPorComanda(Long comandaId) {
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null) {
            throw new WebApplicationException("Usuário não autenticado", Response.Status.UNAUTHORIZED);
        }
        Comanda comanda = resolverComandaDaEmpresa(comandaId, logado);
        return repository.findByComanda(comanda).stream()
                .map(PagamentoMultiResponseDTO::new)
                .collect(Collectors.toList());
    }

    // ===================== helpers =====================

    private Comanda resolverComandaDaEmpresa(Long comandaId, Usuario logado) {
        if (comandaId == null) {
            throw new NotFoundException("Comanda não encontrada");
        }
        Comanda c = comandaRepository.findById(comandaId);
        if (c == null || !Boolean.TRUE.equals(c.getAtivo())) {
            throw new NotFoundException("Comanda não encontrada");
        }
        Empresa empresa = logado.getEmpresa();
        if (empresa == null || empresa.getComandas() == null
                || empresa.getComandas().stream().noneMatch(x -> x != null && c.getId().equals(x.getId()))) {
            throw new NotFoundException("Comanda não encontrada");
        }
        return c;
    }

    private ModoPagamento parseModo(String modo) {
        if (modo == null || modo.trim().isEmpty()) {
            return ModoPagamento.SIMPLES;
        }
        try {
            return ModoPagamento.valueOf(modo.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(
                    "Modo inválido (esperado SIMPLES|RATEADO): " + modo,
                    Response.Status.BAD_REQUEST);
        }
    }

    private BigDecimal somaPagamentosNaoEstornados(Comanda c) {
        return repository.findByComanda(c).stream()
                .filter(p -> !Boolean.TRUE.equals(p.getEstornado()))
                .map(p -> p.getValorTotal() != null
                        ? p.getValorTotal()
                        : (p.getValorPagamento() == null ? BigDecimal.ZERO
                                : BigDecimal.valueOf(p.getValorPagamento())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validarRateado(Comanda comanda, PagamentoMultiInputDTO dto) {
        // 1) soma valorAbatido = valorTotal (com tolerância)
        BigDecimal somaAbatido = dto.itens().stream()
                .map(i -> i.valorAbatido() == null ? BigDecimal.ZERO : i.valorAbatido())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (somaAbatido.subtract(dto.valorTotal()).abs().compareTo(TOLERANCIA) > 0) {
            throw new WebApplicationException(
                    "Soma de valorAbatido dos itens (" + somaAbatido
                            + ") difere do valorTotal (" + dto.valorTotal() + ")",
                    422);
        }

        // 2) cada item pertence à comanda + qtd <= restante
        // Coleta IDs dos itens da comanda
        List<Long> idsItensDaComanda = new ArrayList<>();
        if (comanda.getPedidos() != null) {
            for (Pedido p : comanda.getPedidos()) {
                if (p == null || !Boolean.TRUE.equals(p.getAtivo())) {
                    continue;
                }
                if (p.getItemCompras() != null) {
                    for (ItemCompra ic : p.getItemCompras()) {
                        if (ic != null && ic.getId() != null) {
                            idsItensDaComanda.add(ic.getId());
                        }
                    }
                }
            }
        }

        for (var input : dto.itens()) {
            if (input.itemCompraId() == null || input.quantidade() == null
                    || input.quantidade() <= 0 || input.valorAbatido() == null) {
                throw new WebApplicationException(
                        "Item inválido: itemCompraId, quantidade(>0) e valorAbatido são obrigatórios",
                        Response.Status.BAD_REQUEST);
            }
            if (!idsItensDaComanda.contains(input.itemCompraId())) {
                throw new WebApplicationException(
                        "ItemCompra " + input.itemCompraId() + " não pertence à comanda " + comanda.getId(),
                        422);
            }
            ItemCompra ic = itemCompraRepository.findById(input.itemCompraId());
            if (ic == null) {
                throw new WebApplicationException(
                        "ItemCompra " + input.itemCompraId() + " não encontrado",
                        Response.Status.NOT_FOUND);
            }
            int qtdTotal = ic.getQuantidade() == null ? 0 : ic.getQuantidade();
            int qtdJaPaga = quantidadeJaPaga(comanda, ic.getId());
            int restante = qtdTotal - qtdJaPaga;
            if (input.quantidade() > restante) {
                throw new WebApplicationException(
                        "Quantidade do item " + ic.getId() + " excede o restante ("
                                + restante + ")",
                        422);
            }
        }
    }

    private int quantidadeJaPaga(Comanda comanda, Long itemCompraId) {
        int total = 0;
        for (Pagamento p : repository.findByComanda(comanda)) {
            if (Boolean.TRUE.equals(p.getEstornado())) {
                continue;
            }
            if (p.getItens() == null) {
                continue;
            }
            for (PagamentoItem pi : p.getItens()) {
                if (pi == null || pi.getItemCompra() == null) {
                    continue;
                }
                if (itemCompraId.equals(pi.getItemCompra().getId())) {
                    total += pi.getQuantidade() == null ? 0 : pi.getQuantidade();
                }
            }
        }
        return total;
    }

    @Override
    public PagedResponse<PagamentoResponseDTO> list(
            Long caixaId,
            Integer formaPagamento,
            LocalDate from,
            LocalDate to,
            Long usuarioId,
            int page,
            int size) {

        int pg = Math.max(0, page);
        int sz = Math.min(Math.max(1, size), 100);

        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null || logado.getEmpresa() == null) {
            return new PagedResponse<>(List.of(), pg, sz, 0L);
        }
        Empresa empresa = logado.getEmpresa();

        // Multi-tenant: usuario do caixa pertence à empresa do logado
        StringBuilder ql = new StringBuilder("usuarioCaixa.empresa = :empresa and ativo = true");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("empresa", empresa);

        if (caixaId != null) {
            ql.append(" and caixa.id = :caixaId");
            params.put("caixaId", caixaId);
        }
        if (formaPagamento != null) {
            FormaPagamento fp;
            try {
                fp = FormaPagamento.valueOf(formaPagamento);
            } catch (IllegalArgumentException e) {
                throw new WebApplicationException(
                        "formaPagamento invalido: " + formaPagamento,
                        Response.Status.BAD_REQUEST);
            }
            ql.append(" and formaPagamento = :forma");
            params.put("forma", fp);
        }
        if (usuarioId != null) {
            ql.append(" and usuarioCaixa.id = :usuarioId");
            params.put("usuarioId", usuarioId);
        }
        if (from != null) {
            ql.append(" and dataInclusao >= :from");
            params.put("from", from.atStartOfDay());
        }
        if (to != null) {
            ql.append(" and dataInclusao <= :to");
            params.put("to", to.atTime(LocalTime.MAX));
        }

        long total = repository.count(ql.toString(), params);
        List<Pagamento> pagamentos = repository
                .find(ql.toString(), Sort.by("dataInclusao").descending(), params)
                .page(Page.of(pg, sz))
                .list();

        List<PagamentoResponseDTO> data = pagamentos.stream()
                .map(p -> new PagamentoResponseDTO(
                        p.getComanda() != null ? p.getComanda().getId() : null,
                        p.getPagamentoRealizado(),
                        p.getFormaPagamento(),
                        p.getUsuarioCaixa() != null ? p.getUsuarioCaixa().getId() : null,
                        p.getValorPagamento()))
                .collect(Collectors.toList());

        return new PagedResponse<>(data, pg, sz, total);
    }

}
