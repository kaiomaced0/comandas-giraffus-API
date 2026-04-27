package k.service;

import jakarta.ws.rs.core.Response;
import k.dto.UsuarioLogadoSenhaDTO;
import k.dto.UsuarioUpdateEmailDTO;
import k.dto.UsuarioUpdateLoginDTO;
import k.model.Usuario;

public interface UsuarioLogadoService {
    public Usuario getPerfilUsuarioLogado();

    public Response getPerfilUsuarioLogadoResponse();

    public Response updateSenha(UsuarioLogadoSenhaDTO usuarioLogadoSenhaDTO);

    public Response updateLogin(UsuarioUpdateLoginDTO usuarioUpdateSenha);

    public Response updateEmail(UsuarioUpdateEmailDTO usuarioUpdateSenha);

}
