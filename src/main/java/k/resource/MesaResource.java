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
import k.dto.ComandaResponseDTO;
import k.dto.MesaDTO;
import k.dto.MesaResponseDTO;
import k.service.MesaService;

@Path("/mesa")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MesaResource {

    @Inject
    MesaService service;

    @GET
    @RolesAllowed({"Admin", "Caixa", "Garcom"})
    public List<MesaResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"Admin", "Caixa", "Garcom"})
    public MesaResponseDTO getId(@PathParam("id") Long id) {
        return service.getId(id);
    }

    @POST
    @RolesAllowed({"Admin"})
    public Response insert(MesaDTO mesaDTO) {
        return service.insert(mesaDTO);
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({"Admin"})
    public Response update(@PathParam("id") Long id, MesaDTO mesaDTO) {
        return service.update(id, mesaDTO);
    }

    @PATCH
    @Path("/delete/{id}")
    @RolesAllowed({"Admin"})
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id);
    }

    @GET
    @Path("/{id}/comandas")
    @RolesAllowed({"Admin", "Caixa", "Garcom"})
    public List<ComandaResponseDTO> getComandas(@PathParam("id") Long id) {
        return service.getComandas(id);
    }
}
