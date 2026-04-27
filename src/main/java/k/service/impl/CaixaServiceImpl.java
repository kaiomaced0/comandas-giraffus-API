package k.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import k.model.Empresa;
import k.model.MovimentoCaixa;
import k.model.Perfil;
import k.model.TipoMovimentoCaixa;
import k.model.Usuario;
import k.repository.MovimentoCaixaRepository;
import k.repository.UsuarioRepository;
import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import k.dto.CaixaAbrirInputDTO;
import k.dto.CaixaDTO;
import k.dto.CaixaFecharInputDTO;
import k.dto.CaixaResponseDTO;
import k.model.Caixa;
import k.repository.CaixaRepository;
import k.repository.EmpresaRepository;
import k.service.CaixaService;
import k.service.UsuarioLogadoService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CaixaServiceImpl implements CaixaService {

    public static final Logger LOG = Logger.getLogger(CaixaServiceImpl.class);

    @Inject
    CaixaRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    EmpresaRepository empresaRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    MovimentoCaixaRepository movimentoCaixaRepository;

    @Override
    public List<CaixaResponseDTO> getAll() {
        try {
            LOG.info("Requisição Caixa.getAll()");
            return usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getCaixas().stream()
                    .filter(Caixa::getAtivo)
                    .map(CaixaResponseDTO::new).collect(Collectors.toList());

        } catch (

        Exception e) {
            LOG.error("Erro ao rodar Requisição Caixa.getAll()");
            return null;
        }

    }

    @Override
    public CaixaResponseDTO getCaixaAtual() {
        // Compat: agora retorna o caixa aberto do usuário logado, não mais o
        // Empresa.caixaAtual. Mantido para não quebrar consumidores antigos
        // (rota GET /caixa/atual continua funcional). Se não houver caixa
        // aberto para o usuário, retorna null como antes.
        try {
            LOG.info("Requisição Caixa.getCaixaAtual() (compat -> meuCaixaAberto)");
            Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
            if (logado == null) {
                return null;
            }
            Caixa aberto = repository.findAbertoByUsuario(logado);
            return aberto == null ? null : new CaixaResponseDTO(aberto);
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Caixa.getCaixaAtual()");
            return null;
        }

    }

    @Override
    public List<CaixaResponseDTO> getAllFechadas() {
        try {
            LOG.info("Requisição Caixa.getAll()");
            return usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getCaixas().stream()
                    .filter(Caixa::getAtivo).filter(caixa -> caixa.getFechado())
                    .map(CaixaResponseDTO::new).collect(Collectors.toList());

        } catch (

                Exception e) {
            LOG.error("Erro ao rodar Requisição Caixa.getAll()");
            return null;
        }

    }

    @Override
    public CaixaResponseDTO getId(Long id) {
        try {
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getCaixas()
                    .contains(repository.findById(id)) && repository.findById(id).getAtivo()) {
                LOG.info("Requisição Caixa.getId()");
                return new CaixaResponseDTO(repository.findById(id));
            } else {

                throw new NotFoundException("Sem acesso");
            }
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Caixa.getId()");
            return null;
        }
    }

    @Override
    @Transactional
    public Response insert(CaixaDTO caixaDTO) {
        try {

            LOG.info("Requisição Caixa.insert()");
            Caixa caixa = CaixaDTO.criaCaixa(caixaDTO);
            caixa.setDataInclusao(LocalDateTime.now());
            caixa.setFechado(false);
            caixa.setValorTotal(0.0);
            repository.persist(caixa);
            Usuario u = usuarioRepository.findById(usuarioLogadoService.getPerfilUsuarioLogado().getId());
            u.getEmpresa().getCaixas().add(caixa);
            return Response.status(Status.OK).build();

        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Caixa.insert()");
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

    @Override
    @Transactional
    public Response delete(Long id) {
        try {
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getCaixas()
                    .contains(repository.findById(id))) {
                Caixa entity = repository.findById(id);
                entity.setAtivo(false);
                LOG.info("Requisição Caixa.delete()");
                return Response.status(Status.OK).build();
            } else {
                throw new NotFoundException("Sem acesso");
            }
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Caixa.delete()");
            return Response.status(Status.NO_CONTENT).build();
        }
    }

    @Override
    @Deprecated
    @Transactional
    public Response fechar(Long id) {
        try {
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getCaixas()
                    .contains(repository.findById(id))) {
                Caixa entity = repository.findById(id);
                entity.setFechado(true);
                LOG.info("Requisição Caixa.fechar() [deprecated]");
                return Response.status(Status.OK).build();
            } else {
                throw new NotFoundException("Sem acesso");
            }
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Caixa.fechar()");
            return Response.status(Status.NO_CONTENT).build();
        }
    }

    // ===================== Onda E - Caixa por usuario =====================

    @Override
    @Transactional
    public CaixaResponseDTO abrir(CaixaAbrirInputDTO dto) {
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null) {
            throw new WebApplicationException("Usuário não autenticado", Response.Status.UNAUTHORIZED);
        }
        Caixa jaAberto = repository.findAbertoByUsuario(logado);
        if (jaAberto != null) {
            throw new WebApplicationException("Usuário já possui caixa aberto", Response.Status.CONFLICT);
        }

        BigDecimal valorAbertura = (dto == null || dto.valorAbertura() == null)
                ? BigDecimal.ZERO
                : dto.valorAbertura();
        if (valorAbertura.signum() < 0) {
            throw new WebApplicationException("Valor de abertura não pode ser negativo",
                    Response.Status.BAD_REQUEST);
        }

        Caixa caixa = new Caixa();
        caixa.setUsuario(logado);
        caixa.setValorAbertura(valorAbertura);
        caixa.setValorTotal(0.0);
        caixa.setHoraAbertura(LocalDateTime.now());
        caixa.setDataCaixa(LocalDate.now());
        caixa.setFechado(false);
        if (dto != null && dto.observacao() != null) {
            caixa.setComentario(dto.observacao());
        }
        repository.persist(caixa);

        // NÃO atualiza Empresa.caixaAtual (mantido por compat até remoção em pré-prod)
        Empresa emp = logado.getEmpresa();
        if (emp != null) {
            if (emp.getCaixas() != null) {
                emp.getCaixas().add(caixa);
            }
        }

        LOG.info("Caixa aberto id=" + caixa.getId() + " usuario=" + logado.getId());
        return new CaixaResponseDTO(caixa);
    }

    @Override
    @Transactional
    public CaixaResponseDTO fechar(Long id, CaixaFecharInputDTO dto) {
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null) {
            throw new WebApplicationException("Usuário não autenticado", Response.Status.UNAUTHORIZED);
        }
        if (dto == null || dto.valorFechamentoInformado() == null) {
            throw new WebApplicationException("Valor de fechamento informado é obrigatório",
                    Response.Status.BAD_REQUEST);
        }

        Caixa caixa = findCaixaParaFechamento(id, logado, false);
        return aplicarFechamento(caixa, logado, dto.valorFechamentoInformado(),
                dto.observacoesFechamento(), false, null);
    }

    @Override
    public CaixaResponseDTO meuCaixaAberto() {
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null) {
            throw new WebApplicationException("Usuário não autenticado", Response.Status.UNAUTHORIZED);
        }
        Caixa aberto = repository.findAbertoByUsuario(logado);
        if (aberto == null) {
            throw new WebApplicationException("Nenhum caixa aberto para o usuário",
                    Response.Status.NOT_FOUND);
        }
        return new CaixaResponseDTO(aberto);
    }

    @Override
    public List<CaixaResponseDTO> abertosNaEmpresa() {
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null) {
            throw new WebApplicationException("Usuário não autenticado", Response.Status.UNAUTHORIZED);
        }
        if (!isAdminOuMaster(logado)) {
            throw new WebApplicationException("Apenas Admin/Master podem listar caixas abertos da empresa",
                    Response.Status.FORBIDDEN);
        }
        return repository.findAbertosByEmpresa(logado.getEmpresa()).stream()
                .map(CaixaResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CaixaResponseDTO fecharForcado(Long id, CaixaFecharInputDTO dto, String justificativa) {
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null) {
            throw new WebApplicationException("Usuário não autenticado", Response.Status.UNAUTHORIZED);
        }
        if (!isAdminOuMaster(logado)) {
            throw new WebApplicationException("Apenas Admin/Master podem forçar fechamento",
                    Response.Status.FORBIDDEN);
        }
        if (dto == null || dto.valorFechamentoInformado() == null) {
            throw new WebApplicationException("Valor de fechamento informado é obrigatório",
                    Response.Status.BAD_REQUEST);
        }
        if (justificativa == null || justificativa.trim().isEmpty()) {
            throw new WebApplicationException("Justificativa é obrigatória para fechamento forçado",
                    422);
        }
        if (justificativa.length() > 500) {
            throw new WebApplicationException("Justificativa excede 500 caracteres",
                    Response.Status.BAD_REQUEST);
        }

        Caixa caixa = findCaixaParaFechamento(id, logado, true);
        return aplicarFechamento(caixa, logado, dto.valorFechamentoInformado(),
                dto.observacoesFechamento(), true, justificativa);
    }

    // ===================== helpers =====================

    private boolean isAdminOuMaster(Usuario u) {
        if (u == null || u.getPerfis() == null) {
            return false;
        }
        return u.getPerfis().contains(Perfil.ADMIN) || u.getPerfis().contains(Perfil.MASTER);
    }

    private Caixa findCaixaParaFechamento(Long id, Usuario logado, boolean forcado) {
        if (id == null) {
            throw new NotFoundException("Caixa não encontrado");
        }
        Caixa caixa = repository.findById(id);
        if (caixa == null || !Boolean.TRUE.equals(caixa.getAtivo())) {
            throw new NotFoundException("Caixa não encontrado");
        }
        Empresa empresaLogada = logado.getEmpresa();
        if (empresaLogada == null || empresaLogada.getCaixas() == null
                || empresaLogada.getCaixas().stream()
                        .noneMatch(c -> c != null && caixa.getId().equals(c.getId()))) {
            throw new NotFoundException("Caixa não encontrado");
        }
        if (Boolean.TRUE.equals(caixa.getFechado())) {
            throw new WebApplicationException("Caixa já está fechado", Response.Status.CONFLICT);
        }
        if (!forcado) {
            boolean dono = caixa.getUsuario() != null
                    && caixa.getUsuario().getId() != null
                    && caixa.getUsuario().getId().equals(logado.getId());
            if (!dono && !isAdminOuMaster(logado)) {
                throw new WebApplicationException("Sem permissão para fechar este caixa",
                        Response.Status.FORBIDDEN);
            }
        }
        return caixa;
    }

    private CaixaResponseDTO aplicarFechamento(Caixa caixa, Usuario logado,
            BigDecimal valorInformado, String observacoes,
            boolean forcado, String justificativa) {

        BigDecimal abertura = caixa.getValorAbertura() == null
                ? BigDecimal.ZERO
                : caixa.getValorAbertura();

        // Cálculo do valor esperado:
        // = valorAbertura + soma de pagamentos do caixa + suprimentos - sangrias.
        // Pagamento atual NÃO referencia o Caixa diretamente (só Pagamento.usuarioCaixa).
        // Para evitar mexer em PagamentoService/ComandaService (proibido nesta onda),
        // usamos o agregado já mantido em Caixa.valorTotal como aproximação dos pagamentos.
        // TODO Fase pré-produção: substituir por consulta direta de pagamentos do caixa
        BigDecimal totalPagamentos = caixa.getValorTotal() == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(caixa.getValorTotal());

        BigDecimal somaSuprimentos = BigDecimal.ZERO;
        BigDecimal somaSangrias = BigDecimal.ZERO;
        List<MovimentoCaixa> movs = movimentoCaixaRepository.findByCaixa(caixa);
        if (movs != null) {
            for (MovimentoCaixa m : movs) {
                if (m == null || m.getValor() == null || m.getTipo() == null) {
                    continue;
                }
                if (m.getTipo() == TipoMovimentoCaixa.SUPRIMENTO) {
                    somaSuprimentos = somaSuprimentos.add(m.getValor());
                } else if (m.getTipo() == TipoMovimentoCaixa.SANGRIA) {
                    somaSangrias = somaSangrias.add(m.getValor());
                } else if (m.getTipo() == TipoMovimentoCaixa.TRANSFERENCIA) {
                    // Transferência: se este caixa é a origem, conta como saída;
                    // se é destino, conta como entrada
                    boolean ehOrigem = m.getCaixa() != null
                            && m.getCaixa().getId() != null
                            && m.getCaixa().getId().equals(caixa.getId());
                    if (ehOrigem) {
                        somaSangrias = somaSangrias.add(m.getValor());
                    } else {
                        somaSuprimentos = somaSuprimentos.add(m.getValor());
                    }
                }
            }
        }

        BigDecimal esperado = abertura.add(totalPagamentos).add(somaSuprimentos).subtract(somaSangrias);
        BigDecimal diferenca = valorInformado.subtract(esperado);

        String observacoesFinal = observacoes;
        if (diferenca.signum() != 0) {
            if (observacoes == null || observacoes.trim().isEmpty()) {
                throw new WebApplicationException(
                        "Diferença entre valor informado e esperado exige observações de fechamento",
                        422);
            }
        }
        if (forcado) {
            String prefixo = "FORCADO por " + logado.getLogin() + ": "
                    + (justificativa == null ? "" : justificativa);
            observacoesFinal = (observacoes == null || observacoes.trim().isEmpty())
                    ? prefixo
                    : prefixo + " | " + observacoes;
        }
        if (observacoesFinal != null && observacoesFinal.length() > 500) {
            observacoesFinal = observacoesFinal.substring(0, 500);
        }

        caixa.setValorFechamentoEsperado(esperado);
        caixa.setValorFechamentoInformado(valorInformado);
        caixa.setDiferenca(diferenca);
        caixa.setObservacoesFechamento(observacoesFinal);
        caixa.setHoraFechamento(LocalDateTime.now());
        caixa.setFechado(true);
        caixa.setFechadoPor(logado);

        LOG.info("Caixa fechado id=" + caixa.getId()
                + " esperado=" + esperado
                + " informado=" + valorInformado
                + " diferenca=" + diferenca
                + " forcado=" + forcado);
        return new CaixaResponseDTO(caixa);
    }

}
