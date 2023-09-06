package k.resource;

import java.util.List;

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
import k.dto.ComandaDTO;
import k.dto.ComandaResponseDTO;
import k.service.ComandaService;

@Path("/comanda")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ComandaResource {

    @Inject
    ComandaService service;

    @GET
    @RolesAllowed({ "Admin", "Caixa", "Garcom", "Cozinha" })
    public List<ComandaResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/aberto")
    @RolesAllowed({ "Admin", "Caixa", "Garcom", "Cozinha" })
    public List<ComandaResponseDTO> getEmAberto() {
        return service.getEmAberto();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "Admin", "Caixa", "Garcom", "Cozinha" })
    public ComandaResponseDTO getId(@PathParam("id") Long id) {
        return service.getId(id);
    }

    @GET
    @Path("/nome/{nome}")
    @RolesAllowed({ "Admin", "Caixa", "Garcom", "Cozinha" })
    public ComandaResponseDTO getNome(@PathParam("nome") String nome) {
        return service.getNome(nome);
    }

    @POST
    @Transactional
    @RolesAllowed({ "Admin", "Caixa", "Garcom" })
    public Response insert(ComandaDTO comanda) {
        return service.insert(comanda);
    }

    @PATCH
    @Path("/delete/{id}")
    @Transactional
    @RolesAllowed({ "Admin", "Caixa", "Garcom" })
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id);
    }
}
