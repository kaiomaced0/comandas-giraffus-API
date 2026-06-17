package k.service;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import k.dto.UsuarioLogadoSenhaDTO;
import k.dto.UsuarioUpdateEmailDTO;
import k.dto.UsuarioUpdateLoginDTO;
import k.model.Empresa;
import k.model.Usuario;

public interface UsuarioLogadoService {
    public Usuario getPerfilUsuarioLogado();

    /**
     * Empresa do usuario logado. Lanca 403 (WebApplicationException) se o usuario
     * nao estiver autenticado ou nao possuir empresa vinculada — evita NPE/500 nos
     * services multi-tenant. Default para que stubs/mocks de teste herdem sem
     * precisar reimplementar.
     */
    default Empresa getEmpresaLogada() {
        Usuario user = getPerfilUsuarioLogado();
        if (user == null || user.getEmpresa() == null) {
            throw new WebApplicationException(
                    "Usuario nao possui empresa vinculada.", Response.Status.FORBIDDEN);
        }
        return user.getEmpresa();
    }

    public Response getPerfilUsuarioLogadoResponse();

    public Response updateSenha(UsuarioLogadoSenhaDTO usuarioLogadoSenhaDTO);

    public Response updateLogin(UsuarioUpdateLoginDTO usuarioUpdateSenha);

    public Response updateEmail(UsuarioUpdateEmailDTO usuarioUpdateSenha);

}
