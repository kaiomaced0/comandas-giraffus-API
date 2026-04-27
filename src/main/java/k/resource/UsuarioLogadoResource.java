package k.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.UsuarioLogadoSenhaDTO;
import k.dto.UsuarioUpdateEmailDTO;
import k.dto.UsuarioUpdateLoginDTO;
import k.service.UsuarioLogadoService;

@Path("/usuariologado")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioLogadoResource {

    @Inject
    UsuarioLogadoService service;

    @PermitAll
    @GET
    public Response getPerfilUsuarioLogado() {
        return service.getPerfilUsuarioLogadoResponse();

    }

    @PATCH
    @RolesAllowed({ "Master", "Admin", "Garcom", "Caixa", "Cozinha" })
    @Path("/update/email/{email}")
    @Transactional
    public Response updateEmail(UsuarioUpdateEmailDTO usuarioUpdateEmailDTO) {
        return service.updateEmail(usuarioUpdateEmailDTO);
    }

    @PATCH
    @RolesAllowed({ "Master", "Admin", "Garcom", "Caixa", "Cozinha" })
    @Path("/update/login/{email}")
    @Transactional
    public Response updateLogin(UsuarioUpdateLoginDTO usuarioUpdateLoginDTO) {
        return service.updateLogin(usuarioUpdateLoginDTO);
    }

    @PATCH
    @RolesAllowed({ "Master", "Admin", "Garcom", "Caixa", "Cozinha" })
    @Path("/update/senha")
    @Transactional
    public Response updateSenha(UsuarioLogadoSenhaDTO usuarioLogadoSenhaDTO) {
        return service.updateSenha(usuarioLogadoSenhaDTO);
    }
}
