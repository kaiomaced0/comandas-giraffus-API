package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.model.Usuario;

public interface UsuarioService {

    public List<Usuario> getAll();

    public List<Usuario> getFuncionarios();

    public List<Usuario> getNome(String nome);
    
    public Usuario findByLoginAndSenha(String login, String senha);

    public Usuario findByLogin(String login);

    public List<Usuario> getId();

    public Response insert(Usuario usuario);

    public Response update(@PathParam("idUsuario") Long idUsuario, Usuario usuario);

    public Response delete(@PathParam("id") Long id);
}
