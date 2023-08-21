package k.service;

import jakarta.ws.rs.core.Response;
import k.dto.UsuarioUpdateEmailDTO;
import k.dto.UsuarioUpdateLoginDTO;
import k.dto.UsuarioUpdateSenhaDTO;
import k.model.Usuario;

public interface UsuarioLogadoService {
    public Usuario getPerfilUsuarioLogado();

    public Response updateSenha(UsuarioUpdateSenhaDTO usuarioUpdateSenha);

    public Response updateLogin(UsuarioUpdateLoginDTO usuarioUpdateSenha);

    public Response updateEmail(UsuarioUpdateEmailDTO usuarioUpdateSenha);

}
