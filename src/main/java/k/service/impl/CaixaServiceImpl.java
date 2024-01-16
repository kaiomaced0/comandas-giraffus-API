package k.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import k.model.Usuario;
import k.repository.UsuarioRepository;
import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import k.dto.CaixaDTO;
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
    @Transactional
    public Response fechar(Long id) {
        try {
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getCaixas()
                    .contains(repository.findById(id))) {
                Caixa entity = repository.findById(id);
                entity.setFechado(true);
                LOG.info("Requisição Caixa.fechar()");
                return Response.status(Status.OK).build();
            } else {
                throw new NotFoundException("Sem acesso");
            }
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Caixa.fechar()");
            return Response.status(Status.NO_CONTENT).build();
        }
    }

}
