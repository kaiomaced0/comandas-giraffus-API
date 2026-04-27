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
import k.dto.MesaComandaDTO;
import k.dto.MesaInputDTO;
import k.dto.MesaResponseDTO;
import k.service.MesaService;

@Path("/mesa")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "Master", "Admin", "Caixa", "Garcom" })
public class MesaResource {

    @Inject
    MesaService service;

    @GET
    public List<MesaResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    public MesaResponseDTO getById(@PathParam("id") Long id) {
        return service.getById(id);
    }

    @POST
    public Response insert(MesaInputDTO dto) {
        MesaResponseDTO created = service.insert(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PATCH
    @Path("/{id}")
    public MesaResponseDTO update(@PathParam("id") Long id, MesaInputDTO dto) {
        return service.update(id, dto);
    }

    @PATCH
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/comandas")
    public List<MesaComandaDTO> getComandasAbertas(@PathParam("id") Long id) {
        return service.getComandasAbertas(id);
    }
}
