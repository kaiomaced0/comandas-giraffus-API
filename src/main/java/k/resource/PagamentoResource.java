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
import k.dto.PagamentoDTO;
import k.dto.PagamentoDeleteDTO;
import k.dto.PagamentoResponseDTO;
import k.service.PagamentoService;

@Path("/pagamento")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PagamentoResource {

    @Inject
    PagamentoService service;

    @GET
    @RolesAllowed({ "Admin", "Caixa" })
    public List<PagamentoResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "Admin", "Caixa" })
    public PagamentoResponseDTO getId(@PathParam("id") Long id) {
        return service.getId(id);
    }

    @POST
    @RolesAllowed({ "Admin", "Caixa" })
    @Transactional
    public Response insert(PagamentoDTO Pagamento) {
        return service.insert(Pagamento);
    }

    @PATCH
    @Path("/delete/{id}")
    @RolesAllowed({ "Admin", "Caixa" })
    @Transactional
    public Response delete(@PathParam("id") Long id, String observacao) {
        return service.delete(new PagamentoDeleteDTO(id, observacao));
    }

}
