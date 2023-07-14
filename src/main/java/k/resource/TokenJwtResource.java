package k.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import k.model.Usuario;
import k.service.TokenJwtService;

@ApplicationScoped
public class TokenJwtResource{

    @Inject 
    TokenJwtService service;
    public String generateJwt(Usuario usuario) {
        return service.generateJwt(usuario);
    }

}
