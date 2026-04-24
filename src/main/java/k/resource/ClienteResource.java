package k.resource;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.ClienteDTO;
import k.dto.ClienteResponseDTO;
import k.service.ClienteService;

@Path("/cliente")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteService service;

    @GET
    @RolesAllowed({"Admin", "Caixa"})
    public List<ClienteResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/cpf/{cpf}")
    @RolesAllowed({"Admin", "Caixa"})
    public ClienteResponseDTO getByCpf(@PathParam("cpf") String cpf) {
        return service.getByCpf(cpf);
    }

    @POST
    @RolesAllowed({"Admin", "Caixa"})
    public Response insert(ClienteDTO dto) {
        return service.insertOrFind(dto);
    }
}
