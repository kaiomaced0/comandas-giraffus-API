package k;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import io.quarkus.test.Mock;
import k.dto.UsuarioResponseDTO;
import k.dto.UsuarioUpdateEmailDTO;
import k.dto.UsuarioUpdateLoginDTO;
import k.dto.UsuarioUpdateSenhaDTO;
import k.model.Usuario;
import k.service.UsuarioLogadoService;

@Mock
@ApplicationScoped
public class TestUsuarioLogadoService implements UsuarioLogadoService {

    static volatile Usuario atual;

    @Override
    public Usuario getPerfilUsuarioLogado() {
        return atual;
    }

    @Override
    public Response getPerfilUsuarioLogadoResponse() {
        if (atual == null) {
            return Response.status(401).build();
        }
        return Response.ok(new UsuarioResponseDTO(atual)).build();
    }

    @Override
    public Response updateSenha(UsuarioUpdateSenhaDTO d) {
        return Response.ok().build();
    }

    @Override
    public Response updateLogin(UsuarioUpdateLoginDTO d) {
        return Response.ok().build();
    }

    @Override
    public Response updateEmail(UsuarioUpdateEmailDTO d) {
        return Response.ok().build();
    }
}
