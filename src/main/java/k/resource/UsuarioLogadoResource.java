package k.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import k.dto.UsuarioUpdateEmailDTO;
import k.dto.UsuarioUpdateLoginDTO;
import k.dto.UsuarioUpdateSenhaDTO;
import k.model.Usuario;
import k.service.UsuarioLogadoService;

@ApplicationScoped
public class UsuarioLogadoResource {

    @Inject
    UsuarioLogadoService service;

    public Usuario getPerfilUsuarioLogado() {

        return service.getPerfilUsuarioLogado();

    }

    @PATCH
    @PermitAll
    @Path("/update/email/{email}")
    @Transactional
    public Response updateEmail(UsuarioUpdateEmailDTO usuarioUpdateEmailDTO) {
        return service.updateEmail(usuarioUpdateEmailDTO);
    }

    @PATCH
    @PermitAll
    @Path("/update/login/{email}")
    @Transactional
    public Response updateLogin(UsuarioUpdateLoginDTO usuarioUpdateLoginDTO) {
        return service.updateLogin(usuarioUpdateLoginDTO);
    }

    @PATCH
    @PermitAll
    @Path("/update/email/{email}")
    @Transactional
    public Response updateSenha(UsuarioUpdateSenhaDTO usuarioUpdateSenhaDTO) {
        return service.updateSenha(usuarioUpdateSenhaDTO);
    }
}
