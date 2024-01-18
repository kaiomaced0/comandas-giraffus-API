package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.AuthUsuarioDTO;
import k.dto.UsuarioDTO;
import k.dto.UsuarioResponseDTO;
import k.dto.UsuarioUpdateNomeGerenteDTO;
import k.dto.UsuarioUpdateSenhaGerenteDTO;
import k.model.Usuario;

public interface UsuarioService {

    public List<UsuarioResponseDTO> getAll();

    public List<UsuarioResponseDTO> getFuncionarios();

    public List<UsuarioResponseDTO> getNome(String nome);

    public Usuario findByLoginAndSenha(AuthUsuarioDTO auth);

    public Usuario findByEmailAndSenha(AuthUsuarioDTO auth);

    public UsuarioResponseDTO getId(Long id);

    public Response insert(UsuarioDTO usuario);

    public Response insertFuncionario(UsuarioDTO usuario);

    public Response updateNomeGerente(UsuarioUpdateNomeGerenteDTO usuarioUpdateNome);

    public Response updateSenhaGerente(UsuarioUpdateSenhaGerenteDTO usuarioUpdateSenhaGerente);

    public Response delete(@PathParam("id") Long id);
}
