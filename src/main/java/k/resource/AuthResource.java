package k.resource;

import org.eclipse.microprofile.jwt.JsonWebToken;

import k.dto.AuthUsuarioDTO;
import k.model.Usuario;
import k.service.TokenJwtService;
import k.service.UsuarioService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UsuarioService usuarioService;

    @Inject
    TokenJwtService tokenService;

    @Inject
    JsonWebToken jsonWebToken;

    @POST
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(AuthUsuarioDTO authDTO) {
        Usuario usuario = new Usuario();
        usuario = usuarioService
                .findByLoginAndSenha(authDTO);

        if (usuario == null) {
            usuario = usuarioService.findByEmailAndSenha(authDTO);
            if (usuario == null) {
                return Response.status(Status.NO_CONTENT)
                        .entity("Usuario não encontrado").build();
            }

        } else if (!usuario.getAtivo()) {
            return Response.status(Status.NO_CONTENT)
                    .entity("Usuario Inativo").build();
        }
        return Response.ok()
                .header("Authorization", tokenService.generateJwt(usuario))
                .build();

    }
}
