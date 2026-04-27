package k.resource;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.ClienteInputDTO;
import k.dto.ClienteResponseDTO;
import k.service.ClienteService;

@Path("/cliente")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "Master", "Admin", "Caixa" })
public class ClienteResource {

    @Inject
    ClienteService service;

    @POST
    public ClienteResponseDTO insertOrGet(ClienteInputDTO dto) {
        return service.insertOrGet(dto);
    }

    @GET
    @Path("/cpf/{cpf}")
    public ClienteResponseDTO findByCpf(@PathParam("cpf") String cpf) {
        return service.findByCpf(cpf);
    }

    @GET
    public List<ClienteResponseDTO> getAll() {
        return service.getAll();
    }

    @PATCH
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
