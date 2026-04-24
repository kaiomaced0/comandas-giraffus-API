package k.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import k.dto.AbrirCaixaDTO;
import k.dto.CaixaResponseDTO;
import k.dto.FecharCaixaDTO;
import k.dto.FecharForcadoDTO;
import k.exception.BusinessException;
import k.model.Caixa;
import k.model.FormaPagamento;
import k.model.MovimentoCaixa;
import k.model.Pagamento;
import k.model.Usuario;
import k.model.enums.TipoMovimentoCaixa;
import k.repository.CaixaRepository;
import k.repository.MovimentoCaixaRepository;
import k.repository.UsuarioRepository;
import k.service.CaixaService;
import k.service.UsuarioLogadoService;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CaixaServiceImpl implements CaixaService {

    public static final Logger LOG = Logger.getLogger(CaixaServiceImpl.class);

    @Inject
    CaixaRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    MovimentoCaixaRepository movimentoCaixaRepository;

    @Override
    public List<CaixaResponseDTO> getAll() {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        return repository.findPorEmpresa(u.getEmpresa()).stream()
                .map(CaixaResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public CaixaResponseDTO getCaixaMeu() {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Caixa aberto = repository.findAbertoPorUsuario(u);
        return aberto == null ? null : new CaixaResponseDTO(aberto);
    }

    @Override
    public List<CaixaResponseDTO> getAbertosDaEmpresa() {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        return repository.findAbertosPorEmpresa(u.getEmpresa()).stream()
                .map(CaixaResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<CaixaResponseDTO> getAllFechadas() {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        return repository.findPorEmpresa(u.getEmpresa()).stream()
                .filter(c -> Boolean.TRUE.equals(c.getFechado()))
                .map(CaixaResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public CaixaResponseDTO getId(Long id) {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Caixa c = repository.findById(id);
        if (c == null || c.getEmpresa() == null || !c.getEmpresa().getId().equals(u.getEmpresa().getId())) {
            throw new NotFoundException("Caixa não encontrado");
        }
        return new CaixaResponseDTO(c);
    }

    @Override
    @Transactional
    public Response abrir(AbrirCaixaDTO dto) {
        if (dto == null || dto.valorAbertura() == null) {
            throw new BusinessException("valorAbertura é obrigatório");
        }
        if (dto.valorAbertura().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("valorAbertura não pode ser negativo");
        }
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Caixa existente = repository.findAbertoPorUsuario(u);
        if (existente != null) {
            throw new BusinessException(Status.CONFLICT,
                    "Usuário já possui caixa aberto (id=" + existente.getId() + ")");
        }
        Caixa caixa = new Caixa();
        caixa.setUsuario(u);
        caixa.setEmpresa(u.getEmpresa());
        caixa.setNome(dto.nome() == null
                ? ("Caixa " + u.getLogin() + " " + LocalDate.now())
                : dto.nome());
        caixa.setComentario(dto.comentario());
        caixa.setValorAbertura(dto.valorAbertura());
        caixa.setValorTotal(BigDecimal.ZERO);
        caixa.setHoraAbertura(LocalDateTime.now());
        caixa.setDataCaixa(LocalDate.now());
        caixa.setFechado(false);
        caixa.setComandas(new ArrayList<>());
        caixa.setPagamentos(new ArrayList<>());
        repository.persist(caixa);
        return Response.ok(new CaixaResponseDTO(caixa)).build();
    }

    @Override
    @Transactional
    public Response fechar(FecharCaixaDTO dto) {
        if (dto == null || dto.valorFechamentoInformado() == null) {
            throw new BusinessException("valorFechamentoInformado é obrigatório");
        }
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Caixa caixa = repository.findAbertoPorUsuario(u);
        if (caixa == null) {
            throw new NotFoundException("Usuário não possui caixa aberto");
        }
        return aplicarFechamento(caixa, u, dto.valorFechamentoInformado(), dto.observacoesFechamento(), null);
    }

    @Override
    @Transactional
    public Response fecharForcado(Long idCaixa, FecharForcadoDTO dto) {
        if (dto == null || dto.justificativa() == null || dto.justificativa().isBlank()) {
            throw new BusinessException("justificativa é obrigatória para fechamento forçado");
        }
        Usuario admin = usuarioLogadoService.getPerfilUsuarioLogado();
        Caixa caixa = repository.findById(idCaixa);
        if (caixa == null || caixa.getEmpresa() == null
                || !caixa.getEmpresa().getId().equals(admin.getEmpresa().getId())) {
            throw new NotFoundException("Caixa não encontrado");
        }
        if (Boolean.TRUE.equals(caixa.getFechado())) {
            throw new BusinessException("Caixa já está fechado");
        }
        BigDecimal esperado = calcularValorFechamentoEsperado(caixa);
        return aplicarFechamento(caixa, admin, esperado, dto.justificativa(), admin);
    }

    private Response aplicarFechamento(Caixa caixa, Usuario quemFecha, BigDecimal valorInformado,
            String observacoes, Usuario fechadoPorOverride) {
        BigDecimal esperado = calcularValorFechamentoEsperado(caixa);
        BigDecimal diferenca = valorInformado.subtract(esperado);
        if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
            if (observacoes == null || observacoes.isBlank()) {
                throw new BusinessException(
                        "Diferença de fechamento detectada (" + diferenca + "); observacoesFechamento é obrigatória");
            }
        }
        caixa.setValorFechamentoEsperado(esperado);
        caixa.setValorFechamentoInformado(valorInformado);
        caixa.setDiferenca(diferenca);
        caixa.setObservacoesFechamento(observacoes);
        caixa.setHoraFechamento(LocalDateTime.now());
        caixa.setFechado(true);
        caixa.setFechadoPor(fechadoPorOverride != null ? fechadoPorOverride : quemFecha);
        return Response.ok(new CaixaResponseDTO(caixa)).build();
    }

    @Override
    @Transactional
    public Response delete(Long id) {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Caixa caixa = repository.findById(id);
        if (caixa == null || caixa.getEmpresa() == null
                || !caixa.getEmpresa().getId().equals(u.getEmpresa().getId())) {
            throw new NotFoundException("Caixa não encontrado");
        }
        caixa.setAtivo(false);
        return Response.ok().build();
    }

    @Override
    public BigDecimal calcularValorFechamentoEsperado(Caixa caixa) {
        BigDecimal abertura = caixa.getValorAbertura() != null ? caixa.getValorAbertura() : BigDecimal.ZERO;
        BigDecimal pagamentosDinheiro = BigDecimal.ZERO;
        List<Pagamento> pags = caixa.getPagamentos() != null ? caixa.getPagamentos() : Collections.emptyList();
        for (Pagamento p : pags) {
            if (Boolean.TRUE.equals(p.getEstornado())) {
                continue;
            }
            if (p.getFormaPagamento() == FormaPagamento.AVISTA
                    && p.getValorTotal() != null) {
                pagamentosDinheiro = pagamentosDinheiro.add(p.getValorTotal());
            }
        }
        BigDecimal sangrias = BigDecimal.ZERO;
        BigDecimal suprimentos = BigDecimal.ZERO;
        BigDecimal transferencias = BigDecimal.ZERO;
        List<MovimentoCaixa> movs = movimentoCaixaRepository.findByCaixa(caixa);
        for (MovimentoCaixa m : movs) {
            BigDecimal v = m.getValor() != null ? m.getValor() : BigDecimal.ZERO;
            if (m.getTipo() == TipoMovimentoCaixa.SANGRIA) {
                sangrias = sangrias.add(v);
            } else if (m.getTipo() == TipoMovimentoCaixa.SUPRIMENTO) {
                suprimentos = suprimentos.add(v);
            } else if (m.getTipo() == TipoMovimentoCaixa.TRANSFERENCIA) {
                if (m.getCaixa() != null && m.getCaixa().getId().equals(caixa.getId())) {
                    transferencias = transferencias.subtract(v);
                }
                if (m.getCaixaDestino() != null && m.getCaixaDestino().getId().equals(caixa.getId())) {
                    transferencias = transferencias.add(v);
                }
            }
        }
        return abertura
                .add(pagamentosDinheiro)
                .subtract(sangrias)
                .add(suprimentos)
                .add(transferencias);
    }
}
