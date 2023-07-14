package k.service.impl;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import k.model.Usuario;
import k.repository.UsuarioRepository;
import k.resource.HashResource;
import k.service.UsuarioLogadoService;

public class UsuarioLogadoServiceImpl implements UsuarioLogadoService {
    
    public static final Logger LOG = Logger.getLogger(UsuarioLogadoServiceImpl.class);

    @Inject
    JsonWebToken jsonWebToken;

    @Inject
    HashResource hash;

    @Inject
    UsuarioRepository usuarioRepository;
  
    public Usuario getPerfilUsuarioLogado() {

        try {
            LOG.info("Requisição UsuarioLogado.getPerfilUsuarioLogado()");

            String login = jsonWebToken.getSubject();
            Usuario user = usuarioRepository.findByLogin(login);

            return user;
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição UsuarioLogado.getPerfilUsuarioLogado()");
            return null;
        }

    }
}
