package k.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import k.model.Usuario;
import k.service.UsuarioLogadoService;

@ApplicationScoped
public class UsuarioLogadoResource{

    @Inject
    UsuarioLogadoService service;
    public Usuario getPerfilUsuarioLogado() {

        return service.getPerfilUsuarioLogado();

    }
    
}
