package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.model.Usuario;

public interface UsuarioLogadoService {
    public Usuario getPerfilUsuarioLogado();
}
