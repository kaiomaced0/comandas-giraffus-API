package k.service.impl;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import k.dto.DocumentoFiscalResponseDTO;
import k.dto.EmitirFiscalConsolidadoDTO;
import k.dto.EmitirFiscalDTO;
import k.exception.BusinessException;
import k.model.Cliente;
import k.model.Comanda;
import k.model.DocumentoFiscal;
import k.model.Pagamento;
import k.model.Usuario;
import k.model.enums.StatusEmissaoFiscal;
import k.model.enums.TipoDocumentoFiscal;
import k.repository.ClienteRepository;
import k.repository.ComandaRepository;
import k.repository.DocumentoFiscalRepository;
import k.repository.PagamentoRepository;
import k.service.FiscalService;
import k.service.UsuarioLogadoService;
import k.service.fiscal.FiscalProvider;

@ApplicationScoped
public class FiscalServiceImpl implements FiscalService {

    @Inject
    FiscalProvider provider;

    @Inject
    DocumentoFiscalRepository repository;

    @Inject
    ComandaRepository comandaRepository;

    @Inject
    PagamentoRepository pagamentoRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    @Transactional
    public Response emitirDaComanda(Long idComanda, EmitirFiscalDTO dto) {
        Comanda comanda = comandaRepository.findById(idComanda);
        if (comanda == null) {
            throw new NotFoundException("Comanda não encontrada");
        }
        Cliente cliente = resolverCliente(dto);
        List<Pagamento> pagamentos = comanda.getPagamentos() == null
                ? new ArrayList<>()
                : new ArrayList<>(comanda.getPagamentos().stream()
                        .filter(p -> !Boolean.TRUE.equals(p.getEstornado()))
                        .toList());
        DocumentoFiscal d = provider.emitir(comanda, cliente, pagamentos, tipoOuPadrao(dto));
        d.setUsuarioEmissao(usuarioLogadoService.getPerfilUsuarioLogado());
        repository.persist(d);
        return Response.ok(new DocumentoFiscalResponseDTO(d)).build();
    }

    @Override
    @Transactional
    public Response emitirDoPagamento(Long idPagamento, EmitirFiscalDTO dto) {
        Pagamento p = pagamentoRepository.findById(idPagamento);
        if (p == null) {
            throw new NotFoundException("Pagamento não encontrado");
        }
        Cliente cliente = resolverCliente(dto);
        List<Pagamento> lista = new ArrayList<>();
        lista.add(p);
        DocumentoFiscal d = provider.emitir(p.getComanda(), cliente, lista, tipoOuPadrao(dto));
        d.setUsuarioEmissao(usuarioLogadoService.getPerfilUsuarioLogado());
        repository.persist(d);
        return Response.ok(new DocumentoFiscalResponseDTO(d)).build();
    }

    @Override
    @Transactional
    public Response emitirConsolidado(EmitirFiscalConsolidadoDTO dto) {
        if (dto == null || dto.pagamentoIds() == null || dto.pagamentoIds().isEmpty()) {
            throw new BusinessException("Lista de pagamentoIds obrigatória");
        }
        List<Pagamento> pagamentos = new ArrayList<>();
        Comanda comanda = null;
        for (Long id : dto.pagamentoIds()) {
            Pagamento p = pagamentoRepository.findById(id);
            if (p == null) {
                throw new NotFoundException("Pagamento " + id + " não encontrado");
            }
            if (comanda == null) {
                comanda = p.getComanda();
            }
            pagamentos.add(p);
        }
        Cliente cliente = dto.clienteId() == null ? null : clienteRepository.findById(dto.clienteId());
        TipoDocumentoFiscal tipo = dto.tipo() == null ? TipoDocumentoFiscal.NFCE : dto.tipo();
        DocumentoFiscal d = provider.emitir(comanda, cliente, pagamentos, tipo);
        d.setUsuarioEmissao(usuarioLogadoService.getPerfilUsuarioLogado());
        repository.persist(d);
        return Response.ok(new DocumentoFiscalResponseDTO(d)).build();
    }

    @Override
    public Response getId(Long id) {
        DocumentoFiscal d = repository.findById(id);
        if (d == null) {
            throw new NotFoundException("Documento fiscal não encontrado");
        }
        return Response.ok(new DocumentoFiscalResponseDTO(d)).build();
    }

    @Override
    @Transactional
    public Response cancelar(Long id) {
        DocumentoFiscal d = repository.findById(id);
        if (d == null) {
            throw new NotFoundException("Documento fiscal não encontrado");
        }
        if (d.getStatusEmissao() == StatusEmissaoFiscal.CANCELADO) {
            throw new BusinessException("Documento fiscal já está cancelado");
        }
        provider.cancelar(d);
        return Response.ok(new DocumentoFiscalResponseDTO(d)).build();
    }

    private Cliente resolverCliente(EmitirFiscalDTO dto) {
        if (dto == null || dto.clienteId() == null) {
            return null;
        }
        return clienteRepository.findById(dto.clienteId());
    }

    private TipoDocumentoFiscal tipoOuPadrao(EmitirFiscalDTO dto) {
        if (dto == null || dto.tipo() == null) {
            return TipoDocumentoFiscal.NFCE;
        }
        return dto.tipo();
    }

    void setProvider(FiscalProvider provider) {
        this.provider = provider;
    }

    Usuario getUsuarioLogado() {
        return usuarioLogadoService.getPerfilUsuarioLogado();
    }
}
