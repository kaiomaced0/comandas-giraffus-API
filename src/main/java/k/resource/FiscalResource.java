package k.resource;

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
import k.dto.EmitirFiscalConsolidadoDTO;
import k.dto.EmitirFiscalDTO;
import k.service.FiscalService;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FiscalResource {

    @Inject
    FiscalService service;

    @POST
    @Path("/comanda/{id}/fiscal")
    @RolesAllowed({"Admin", "Caixa"})
    public Response emitirDaComanda(@PathParam("id") Long id, EmitirFiscalDTO dto) {
        return service.emitirDaComanda(id, dto);
    }

    @POST
    @Path("/pagamento/{id}/fiscal")
    @RolesAllowed({"Admin", "Caixa"})
    public Response emitirDoPagamento(@PathParam("id") Long id, EmitirFiscalDTO dto) {
        return service.emitirDoPagamento(id, dto);
    }

    @POST
    @Path("/fiscal/consolidado")
    @RolesAllowed({"Admin", "Caixa"})
    public Response emitirConsolidado(EmitirFiscalConsolidadoDTO dto) {
        return service.emitirConsolidado(dto);
    }

    @GET
    @Path("/fiscal/{id}")
    @RolesAllowed({"Admin", "Caixa"})
    public Response getId(@PathParam("id") Long id) {
        return service.getId(id);
    }

    @POST
    @Path("/fiscal/{id}/cancelar")
    @RolesAllowed({"Admin"})
    public Response cancelar(@PathParam("id") Long id) {
        return service.cancelar(id);
    }
}
