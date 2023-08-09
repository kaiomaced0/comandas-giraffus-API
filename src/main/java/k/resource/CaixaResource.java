package k.resource;

import java.util.List;

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
import k.dto.CaixaDTO;
import k.dto.CaixaResponseDTO;
import k.service.CaixaService;

@Path("/caixa")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CaixaResource {

    @Inject
    CaixaService service;

    @GET
    public List<CaixaResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    public CaixaResponseDTO getId(@PathParam("id") Long id) {
        return service.getId(id);
    }

    @POST
    public Response insert(CaixaDTO caixa) {
        return service.insert(caixa);
    }

    @PATCH
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id);
    }

    @PATCH
    @Path("/fechar/{id}")
    public Response fechar(@PathParam("id") Long id) {
        return service.fechar(id);
    }
}
