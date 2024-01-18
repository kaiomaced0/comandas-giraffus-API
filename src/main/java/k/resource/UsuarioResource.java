package k.resource;

import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.UsuarioDTO;
import k.dto.UsuarioResponseDTO;
import k.dto.UsuarioUpdateNomeGerenteDTO;
import k.dto.UsuarioUpdateSenhaGerenteDTO;
import k.service.UsuarioService;

@Path("/usuario")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    UsuarioService service;

    @RolesAllowed({ "Master" })
    @GET
    @Path("/todos")
    public List<UsuarioResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @Path("funcionarios/nome/{nome}")
    @RolesAllowed({ "Admin" })
    public List<UsuarioResponseDTO> getNome(@PathParam("nome") String nome) {
        return service.getNome(nome);
    }

    @GET
    @Path("{id}")
    @RolesAllowed({ "Admin" })
    public UsuarioResponseDTO getId(@PathParam("id") Long id) {
        return service.getId(id);
    }

    @POST
    @PermitAll
    public Response insert(UsuarioDTO usuarioDTO) {
        return service.insert(usuarioDTO);
    }

    @PATCH
    @Path("/delete/{id}")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id);
    }



}
