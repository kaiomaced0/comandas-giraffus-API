package k.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.AuthUsuarioDTO;
import k.dto.UsuarioDTO;
import k.dto.UsuarioResponseDTO;
import k.dto.UsuarioUpdateNomeGerenteDTO;
import k.dto.UsuarioUpdateSenhaGerenteDTO;
import k.model.Perfil;
import k.model.Usuario;
import k.repository.EmpresaRepository;
import k.repository.UsuarioRepository;
import k.service.HashService;
import k.service.UsuarioLogadoService;
import k.service.UsuarioService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsuarioServiceImpl implements UsuarioService {

    public static final Logger LOG = Logger.getLogger(UsuarioServiceImpl.class);

    @Inject
    UsuarioRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    EmpresaRepository empresaRepository;

    @Inject
    HashService hash;

    @Override
    public List<UsuarioResponseDTO> getAll() {
        try {
            LOG.info("Requisicao Usuario.getAll()");
            return repository.findAll().stream()
                    .map(UsuarioResponseDTO::new).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao Usuario.getAll()");
            return null;
        }
    }

    @Override
    public List<UsuarioResponseDTO> getNome(String nome) {
        try {
            LOG.info("Requisicao Usuario.getAll()");
            return repository.findByNome(nome).stream()
                    .filter(usuario -> usuario.getEmpresa().getId() == usuarioLogadoService.getPerfilUsuarioLogado()
                            .getEmpresa().getId())
                    .map(UsuarioResponseDTO::new).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao Usuario.getAll()");
            return null;
        }
    }

    @Override
    public Usuario findByLoginAndSenha(AuthUsuarioDTO auth) {
        try {
            LOG.info("Requisicao Usuario.findByLoginAndSenha()");
            return repository.findByLoginAndSenha(auth.login(), hash.getHashSenha(auth.senha()));

        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao Usuario.findByLoginAndSenha()");
            return null;
        }
    }

    @Override
    public Usuario findByEmailAndSenha(AuthUsuarioDTO auth) {
        try {
            LOG.info("Requisicao Usuario.findByLoginAndSenha()");
            return repository.findByEmailAndSenha(auth.login(), hash.getHashSenha(auth.senha()));

        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao Usuario.findByLoginAndSenha()");
            return null;
        }
    }

    @Override
    public UsuarioResponseDTO getId(Long id) {
        try {
            Usuario u = repository.findById(id);
            if (u.getEmpresa() == usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa()) {
                LOG.info("Requisicao Usuario.getId()");
                return new UsuarioResponseDTO(u);
            } else {
                throw new Exception();
            }

        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao Usuario.getId()");
            return null;
        }
    }

    @Override
    @Transactional
    public Response insert(UsuarioDTO usuario) {
        try {
            Usuario entity = UsuarioDTO.criaUsuario(usuario);
            entity.setSenha(hash.getHashSenha(usuario.senha()));
            // entity.setEmpresa(empresaRepository.findById(usuario.idEmpresa()));
            entity.setPerfis(new HashSet<Perfil>());
            entity.getPerfis().add(Perfil.valueOf(usuario.idPerfil()));
            repository.persist(entity);

            LOG.info("Requisicao Usuario.insert()");
            return Response.ok(entity).build();
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao Usuario.insert()");
            return null;
        }
    }

    @Override
    @Transactional
    public Response delete(Long id) {
        try {
            Usuario entity = repository.findById(id);
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa() == entity.getEmpresa()) {
                if (usuarioLogadoService.getPerfilUsuarioLogado().getId() == usuarioLogadoService
                        .getPerfilUsuarioLogado()
                        .getEmpresa().getAdmin().getId()) {
                    if (usuarioLogadoService.getPerfilUsuarioLogado().getId() != entity.getId()) {
                        repository.delete(entity);
                        LOG.info("Requisicao Usuario.delete()");
                        return Response.ok(new UsuarioResponseDTO(entity)).build();
                    } else {
                        throw new Exception();
                    }
                } else {
                    throw new Exception();
                }
            } else {
                throw new Exception();

            }
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao Usuario.delete()");
            return Response.status(Status.STATUS_NO_TRANSACTION).build();

        }

    }

    @Override
    public List<UsuarioResponseDTO> getFuncionarios() {
        try {
            LOG.info("Requisicao Usuario.getFuncionarios()");
            return repository.findAll().stream().filter(
                    usuario -> usuario.getEmpresa() == usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa())
                    .map(UsuarioResponseDTO::new).collect(Collectors.toList());

        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao UsuariogetFuncionarios()");
            return null;
        }
    }

    @Override
    @Transactional
    public Response updateNomeGerente(UsuarioUpdateNomeGerenteDTO usuarioUpdateNome) {
        try {
            LOG.info("Requisicao Usuario.updateNome()");
            Usuario entity = repository.findById(usuarioUpdateNome.id());
            entity.setNome(usuarioUpdateNome.nome());
            return Response.ok(new UsuarioResponseDTO(entity)).build();
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao Usuario.updateNome()");
            return null;
        }

    }

    @Override
    @Transactional
    public Response updateSenhaGerente(UsuarioUpdateSenhaGerenteDTO usuarioUpdateSenhaGerente) {
        try {
            Usuario entity = repository.findById(usuarioUpdateSenhaGerente.idUsuario());
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa() == entity.getEmpresa()) {

                entity.setSenha(usuarioUpdateSenhaGerente.senha());
                return Response.ok().build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.notModified().build();
        }
    }
}
