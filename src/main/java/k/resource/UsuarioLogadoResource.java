package k.resource;

import org.eclipse.microprofile.jwt.JsonWebToken;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import k.model.Usuario;
import k.repository.UsuarioRepository;

@ApplicationScoped
public class UsuarioLogadoResource{

    public static final Logger LOG = Logger.getLogger(UsuarioLogadoResource.class);

    @Inject
    JsonWebToken jsonWebToken;

    @Inject
    HashResource hash;

    @Inject
    UsuarioRepository usuarioRepository;

    
   
    
    public Usuario getPerfilUsuarioLogado() {

        try {
            LOG.info("Requisição Telefone.getPerfilUsuarioLogado()");

            String login = jsonWebToken.getSubject();
            Usuario user = usuarioRepository.findByLogin(login);

            return user;
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Telefone.getPerfilUsuarioLogado()");
            return null;
        }

    }
    
}
