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
import k.dto.DocumentoFiscalResponseDTO;
import k.dto.FiscalConsolidadoInputDTO;
import k.service.FiscalService;

@Path("/fiscal")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "Master", "Admin", "Caixa" })
public class FiscalResource {

    @Inject
    FiscalService service;

    @GET
    public List<DocumentoFiscalResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    public DocumentoFiscalResponseDTO getById(@PathParam("id") Long id) {
        return service.getById(id);
    }

    @POST
    @Path("/consolidado")
    public Response emitirConsolidado(FiscalConsolidadoInputDTO dto) {
        DocumentoFiscalResponseDTO created = service.emitirConsolidado(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @POST
    @Path("/{id}/cancelar")
    public DocumentoFiscalResponseDTO cancelar(@PathParam("id") Long id) {
        return service.cancelar(id);
    }
}
