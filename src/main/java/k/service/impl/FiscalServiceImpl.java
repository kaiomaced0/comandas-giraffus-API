package k.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import k.dto.DocumentoFiscalResponseDTO;
import k.dto.FiscalConsolidadoInputDTO;
import k.dto.FiscalEmissaoComandaInputDTO;
import k.dto.FiscalEmissaoPagamentoInputDTO;
import k.model.Cliente;
import k.model.Comanda;
import k.model.DocumentoFiscal;
import k.model.Empresa;
import k.model.Pagamento;
import k.model.StatusDocumentoFiscal;
import k.model.TipoDocumentoFiscal;
import k.model.Usuario;
import k.repository.ClienteRepository;
import k.repository.ComandaRepository;
import k.repository.DocumentoFiscalRepository;
import k.repository.PagamentoRepository;
import k.service.FiscalService;
import k.service.UsuarioLogadoService;
import k.service.fiscal.FiscalProvider;

@ApplicationScoped
public class FiscalServiceImpl implements FiscalService {

    private static final Logger LOG = Logger.getLogger(FiscalServiceImpl.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Inject
    DocumentoFiscalRepository repository;

    @Inject
    ComandaRepository comandaRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    PagamentoRepository pagamentoRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    FiscalProvider fiscalProvider;

    @Override
    @Transactional
    public DocumentoFiscalResponseDTO emitirSobreComanda(Long comandaId, FiscalEmissaoComandaInputDTO dto) {
        if (dto == null) {
            throw new WebApplicationException("Body obrigatório", Response.Status.BAD_REQUEST);
        }
        Usuario logado = requireLogado();
        Comanda comanda = resolverComandaDaEmpresa(comandaId, logado);
        Cliente cliente = resolverClienteOpcional(dto.clienteId(), logado);
        TipoDocumentoFiscal tipo = parseTipo(dto.tipo());

        List<Pagamento> pagamentos = pagamentoRepository.findByComanda(comanda).stream()
                .filter(p -> !Boolean.TRUE.equals(p.getEstornado()))
                .collect(Collectors.toList());

        DocumentoFiscal documento = construirDocumento(tipo, comanda, cliente, pagamentos, logado, dto);
        repository.persist(documento);
        LOG.info("DocumentoFiscal emitido id=" + documento.getId()
                + " tipo=" + tipo + " comanda=" + comanda.getId());
        return toResponse(documento);
    }

    @Override
    @Transactional
    public DocumentoFiscalResponseDTO emitirSobrePagamento(Long pagamentoId, FiscalEmissaoPagamentoInputDTO dto) {
        if (dto == null) {
            throw new WebApplicationException("Body obrigatório", Response.Status.BAD_REQUEST);
        }
        Usuario logado = requireLogado();
        Pagamento pagamento = resolverPagamentoDaEmpresa(pagamentoId, logado);
        if (Boolean.TRUE.equals(pagamento.getEstornado())) {
            throw new WebApplicationException("Pagamento estornado não pode emitir documento fiscal", 422);
        }
        Cliente cliente = resolverClienteOpcional(dto.clienteId(), logado);
        TipoDocumentoFiscal tipo = parseTipo(dto.tipo());

        DocumentoFiscal documento = construirDocumento(tipo, pagamento.getComanda(), cliente,
                List.of(pagamento), logado, dto);
        repository.persist(documento);
        LOG.info("DocumentoFiscal emitido id=" + documento.getId()
                + " tipo=" + tipo + " pagamento=" + pagamento.getId());
        return toResponse(documento);
    }

    @Override
    @Transactional
    public DocumentoFiscalResponseDTO emitirConsolidado(FiscalConsolidadoInputDTO dto) {
        if (dto == null) {
            throw new WebApplicationException("Body obrigatório", Response.Status.BAD_REQUEST);
        }
        if (dto.pagamentoIds() == null || dto.pagamentoIds().isEmpty()) {
            throw new WebApplicationException("pagamentoIds é obrigatório", Response.Status.BAD_REQUEST);
        }
        Usuario logado = requireLogado();
        Cliente cliente = resolverClienteOpcional(dto.clienteId(), logado);
        TipoDocumentoFiscal tipo = parseTipo(dto.tipo());

        List<Pagamento> pagamentos = new ArrayList<>();
        Comanda comandaUnica = null;
        boolean mesmaComanda = true;
        for (Long pid : dto.pagamentoIds()) {
            Pagamento p = resolverPagamentoDaEmpresa(pid, logado);
            if (Boolean.TRUE.equals(p.getEstornado())) {
                throw new WebApplicationException("Pagamento estornado não pode ser consolidado: id=" + pid, 422);
            }
            pagamentos.add(p);
            Comanda cp = p.getComanda();
            if (cp == null) {
                mesmaComanda = false;
            } else if (comandaUnica == null) {
                comandaUnica = cp;
            } else if (!comandaUnica.getId().equals(cp.getId())) {
                mesmaComanda = false;
            }
        }
        Comanda comanda = mesmaComanda ? comandaUnica : null;

        DocumentoFiscal documento = construirDocumento(tipo, comanda, cliente, pagamentos, logado, dto);
        repository.persist(documento);
        LOG.info("DocumentoFiscal consolidado emitido id=" + documento.getId()
                + " tipo=" + tipo + " pagamentos=" + dto.pagamentoIds());
        return toResponse(documento);
    }

    @Override
    public DocumentoFiscalResponseDTO getById(Long id) {
        DocumentoFiscal doc = findOwned(id);
        return toResponse(doc);
    }

    @Override
    @Transactional
    public DocumentoFiscalResponseDTO cancelar(Long id) {
        DocumentoFiscal doc = findOwned(id);
        doc.setStatusEmissao(StatusDocumentoFiscal.CANCELADO);
        LOG.info("DocumentoFiscal cancelado id=" + doc.getId());
        return toResponse(doc);
    }

    @Override
    public List<DocumentoFiscalResponseDTO> getAll() {
        Usuario logado = requireLogado();
        Empresa empresa = logado.getEmpresa();
        return repository.findByEmpresa(empresa).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ===================== helpers =====================

    private DocumentoFiscal construirDocumento(TipoDocumentoFiscal tipo, Comanda comanda, Cliente cliente,
            List<Pagamento> pagamentos, Usuario logado, Object payloadInput) {
        DocumentoFiscal documento = new DocumentoFiscal();
        documento.setTipo(tipo);
        documento.setComanda(comanda);
        documento.setCliente(cliente);
        documento.setEmpresa(logado.getEmpresa());
        documento.setUsuarioEmissao(logado);
        documento.setEmulado(fiscalProvider.isEmulado());
        documento.setStatusEmissao(StatusDocumentoFiscal.EMITIDO);
        documento.setEmitidoEm(LocalDateTime.now());
        String numero = fiscalProvider.getNumero();
        String chave = fiscalProvider.getChaveAcesso();
        documento.setNumero(numero);
        documento.setChaveAcesso(chave);
        documento.setPayloadEmissao(toJson(payloadInput));
        documento.setPayloadRetorno(fiscalProvider.getPayloadRetorno(numero, chave));
        documento.setPagamentos(new ArrayList<>(pagamentos));
        return documento;
    }

    private DocumentoFiscal findOwned(Long id) {
        if (id == null) {
            throw new NotFoundException("Documento fiscal não encontrado");
        }
        DocumentoFiscal doc = repository.findById(id);
        if (doc == null || !Boolean.TRUE.equals(doc.getAtivo())) {
            throw new NotFoundException("Documento fiscal não encontrado");
        }
        Usuario logado = requireLogado();
        Empresa empresaLogada = logado.getEmpresa();
        if (doc.getEmpresa() == null || empresaLogada == null
                || !doc.getEmpresa().getId().equals(empresaLogada.getId())) {
            throw new NotFoundException("Documento fiscal não encontrado");
        }
        return doc;
    }

    private Usuario requireLogado() {
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null) {
            throw new WebApplicationException("Usuário não autenticado", Response.Status.UNAUTHORIZED);
        }
        return logado;
    }

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

    private Pagamento resolverPagamentoDaEmpresa(Long pagamentoId, Usuario logado) {
        if (pagamentoId == null) {
            throw new NotFoundException("Pagamento não encontrado");
        }
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId);
        if (pagamento == null || !Boolean.TRUE.equals(pagamento.getAtivo())) {
            throw new NotFoundException("Pagamento não encontrado");
        }
        if (pagamento.getUsuarioCaixa() == null
                || pagamento.getUsuarioCaixa().getEmpresa() == null
                || logado.getEmpresa() == null
                || !pagamento.getUsuarioCaixa().getEmpresa().getId().equals(logado.getEmpresa().getId())) {
            throw new NotFoundException("Pagamento não encontrado");
        }
        return pagamento;
    }

    private Cliente resolverClienteOpcional(Long clienteId, Usuario logado) {
        if (clienteId == null) {
            return null;
        }
        Cliente c = clienteRepository.findById(clienteId);
        if (c == null || !Boolean.TRUE.equals(c.getAtivo())) {
            throw new NotFoundException("Cliente não encontrado");
        }
        Empresa empresaLogada = logado.getEmpresa();
        if (c.getEmpresa() == null || empresaLogada == null
                || !c.getEmpresa().getId().equals(empresaLogada.getId())) {
            throw new NotFoundException("Cliente não encontrado");
        }
        return c;
    }

    private TipoDocumentoFiscal parseTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new WebApplicationException("Tipo é obrigatório (NFCE|NFE)", Response.Status.BAD_REQUEST);
        }
        try {
            return TipoDocumentoFiscal.valueOf(tipo.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(
                    "Tipo inválido (esperado NFCE|NFE): " + tipo,
                    Response.Status.BAD_REQUEST);
        }
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            LOG.warn("Falha ao serializar payload de emissão", e);
            return null;
        }
    }

    private DocumentoFiscalResponseDTO toResponse(DocumentoFiscal doc) {
        List<Long> pagamentoIds = doc.getPagamentos() == null
                ? List.of()
                : doc.getPagamentos().stream()
                        .filter(p -> p != null && p.getId() != null)
                        .map(Pagamento::getId)
                        .collect(Collectors.toList());
        return new DocumentoFiscalResponseDTO(
                doc.getId(),
                doc.getTipo() == null ? null : doc.getTipo().name(),
                doc.getNumero(),
                doc.getChaveAcesso(),
                doc.getStatusEmissao() == null ? null : doc.getStatusEmissao().name(),
                doc.getEmulado(),
                doc.getComanda() == null ? null : doc.getComanda().getId(),
                doc.getCliente() == null ? null : doc.getCliente().getId(),
                doc.getEmitidoEm(),
                pagamentoIds);
    }
}
