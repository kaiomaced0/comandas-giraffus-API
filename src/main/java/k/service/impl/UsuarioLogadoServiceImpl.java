package k.service.impl;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.UsuarioResponseDTO;
import k.dto.UsuarioUpdateEmailDTO;
import k.dto.UsuarioUpdateLoginDTO;
import k.dto.UsuarioUpdateSenhaDTO;
import k.model.Usuario;
import k.repository.UsuarioRepository;
import k.resource.HashResource;
import k.service.UsuarioLogadoService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsuarioLogadoServiceImpl implements UsuarioLogadoService {

    public static final Logger LOG = Logger.getLogger(UsuarioLogadoServiceImpl.class);

    @Inject
    JsonWebToken jsonWebToken;

    @Inject
    HashResource hash;

    @Inject
    UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Response updateSenha(UsuarioUpdateSenhaDTO usuarioUpdateSenha) {
        Usuario entity = usuarioRepository.findById(getPerfilUsuarioLogado().getId());
        try {
            if (usuarioUpdateSenha.senhaAntiga() == entity.getSenha()) {
                LOG.info("Requisicao Usuario.updatupdateSenhaeNome()");
                entity.setSenha(usuarioUpdateSenha.novaSenha());
                return Response.ok(new UsuarioResponseDTO(entity)).build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao Usuario.updateSenha()");
            return Response.notModified().build();
        }
    }

    @Override
    public Usuario getPerfilUsuarioLogado() {

        Usuario user = new Usuario();
        try {
            LOG.info("Requisicao UsuarioLogado.getPerfilUsuarioLogado()");

            String login = jsonWebToken.getSubject();
            user = usuarioRepository.findByLogin(login);

            return user;
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao UsuarioLogado.getPerfilUsuarioLogado()");
            return null;
        }

    }

    @Override
    @Transactional
    public Response updateLogin(UsuarioUpdateLoginDTO usuarioUpdateLoginDTO) {
        Usuario entity = usuarioRepository.findById(getPerfilUsuarioLogado().getId());
        try {
            LOG.info("Requisicao Usuario.updatupdateSenhaeNome()");
            entity.setLogin(usuarioUpdateLoginDTO.login());
            return Response.ok(new UsuarioResponseDTO(entity)).build();
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao Usuario.updateSenha()");
            return Response.notModified().build();
        }
    }

    @Override
    @Transactional
    public Response updateEmail(UsuarioUpdateEmailDTO usuarioUpdateEmailDTO) {
        Usuario entity = usuarioRepository.findById(getPerfilUsuarioLogado().getId());
        try {
            entity.setEmail(usuarioUpdateEmailDTO.email());
            LOG.info("Requisicao Usuario.updateEmail()");
            return Response.ok(new UsuarioResponseDTO(entity)).build();
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisicao Usuario.updateEmail()");
            return Response.notModified().build();
        }
    }

}
