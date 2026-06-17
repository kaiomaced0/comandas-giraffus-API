package k.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import k.dto.MovimentoCaixaInputDTO;
import k.dto.MovimentoCaixaResponseDTO;
import k.dto.TransferenciaInputDTO;
import k.model.Caixa;
import k.model.Empresa;
import k.model.MovimentoCaixa;
import k.model.TipoMovimentoCaixa;
import k.model.Usuario;
import k.repository.CaixaRepository;
import k.repository.MovimentoCaixaRepository;
import k.service.MovimentoCaixaService;
import k.service.UsuarioLogadoService;

@ApplicationScoped
public class MovimentoCaixaServiceImpl implements MovimentoCaixaService {

    public static final Logger LOG = Logger.getLogger(MovimentoCaixaServiceImpl.class);

    @Inject
    MovimentoCaixaRepository repository;

    @Inject
    CaixaRepository caixaRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    @Transactional
    public MovimentoCaixaResponseDTO sangria(Long caixaId, MovimentoCaixaInputDTO dto) {
        Caixa caixa = findOwnedAberto(caixaId);
        BigDecimal valor = validateValor(dto == null ? null : dto.valor());
        String motivo = dto == null ? null : dto.motivo();
        return persist(TipoMovimentoCaixa.SANGRIA, caixa, null, valor, motivo);
    }

    @Override
    @Transactional
    public MovimentoCaixaResponseDTO suprimento(Long caixaId, MovimentoCaixaInputDTO dto) {
        Caixa caixa = findOwnedAberto(caixaId);
        BigDecimal valor = validateValor(dto == null ? null : dto.valor());
        String motivo = dto == null ? null : dto.motivo();
        return persist(TipoMovimentoCaixa.SUPRIMENTO, caixa, null, valor, motivo);
    }

    @Override
    @Transactional
    public MovimentoCaixaResponseDTO transferir(Long caixaOrigemId, TransferenciaInputDTO dto) {
        if (dto == null) {
            throw new WebApplicationException("Body obrigatório", Response.Status.BAD_REQUEST);
        }
        if (dto.caixaDestinoId() == null) {
            throw new WebApplicationException("Caixa destino é obrigatório", Response.Status.BAD_REQUEST);
        }
        if (caixaOrigemId == null || caixaOrigemId.equals(dto.caixaDestinoId())) {
            throw new WebApplicationException("Caixa origem e destino devem ser distintos",
                    Response.Status.BAD_REQUEST);
        }
        Caixa origem = findOwnedAberto(caixaOrigemId);
        Caixa destino = findOwnedAberto(dto.caixaDestinoId());
        BigDecimal valor = validateValor(dto.valor());
        return persist(TipoMovimentoCaixa.TRANSFERENCIA, origem, destino, valor, dto.motivo());
    }

    @Override
    public List<MovimentoCaixaResponseDTO> getByCaixa(Long caixaId) {
        Caixa caixa = findOwned(caixaId);
        return repository.findByCaixa(caixa).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MovimentoCaixaResponseDTO persist(TipoMovimentoCaixa tipo, Caixa caixa, Caixa caixaDestino,
            BigDecimal valor, String motivo) {
        MovimentoCaixa m = new MovimentoCaixa();
        m.setTipo(tipo);
        m.setCaixa(caixa);
        m.setCaixaDestino(caixaDestino);
        m.setValor(valor);
        m.setMotivo(motivo);
        m.setUsuario(usuarioLogadoService.getPerfilUsuarioLogado());
        repository.persist(m);
        LOG.info("MovimentoCaixa criado id=" + m.getId() + " tipo=" + tipo);
        return toResponse(m);
    }

    private BigDecimal validateValor(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new WebApplicationException("Valor deve ser positivo", Response.Status.BAD_REQUEST);
        }
        return valor;
    }

    private Caixa findOwned(Long id) {
        if (id == null) {
            throw new NotFoundException("Caixa não encontrado");
        }
        Caixa caixa = caixaRepository.findById(id);
        if (caixa == null || !Boolean.TRUE.equals(caixa.getAtivo())) {
            throw new NotFoundException("Caixa não encontrado");
        }
        Empresa empresaLogada = usuarioLogadoService.getEmpresaLogada();
        if (empresaLogada.getCaixas() == null
                || empresaLogada.getCaixas().stream().noneMatch(c -> c != null && caixa.getId().equals(c.getId()))) {
            throw new NotFoundException("Caixa não encontrado");
        }
        return caixa;
    }

    private Caixa findOwnedAberto(Long id) {
        Caixa caixa = findOwned(id);
        if (Boolean.TRUE.equals(caixa.getFechado())) {
            throw new WebApplicationException("Caixa está fechado", Response.Status.BAD_REQUEST);
        }
        return caixa;
    }

    private MovimentoCaixaResponseDTO toResponse(MovimentoCaixa m) {
        LocalDate data = m.getDataInclusao() == null ? null : m.getDataInclusao().toLocalDate();
        Long destinoId = m.getCaixaDestino() == null ? null : m.getCaixaDestino().getId();
        Long caixaId = m.getCaixa() == null ? null : m.getCaixa().getId();
        String tipo = m.getTipo() == null ? null : m.getTipo().name();
        return new MovimentoCaixaResponseDTO(m.getId(), tipo, m.getValor(), m.getMotivo(), caixaId, destinoId, data);
    }
}
