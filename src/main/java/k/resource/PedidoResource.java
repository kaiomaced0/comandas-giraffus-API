package k.resource;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.PedidoAdicionaItemCompraDTO;
import k.dto.PedidoDTO;
import k.dto.PedidoRemoveItemCompraDTO;
import k.dto.PedidoResponseDTO;
import k.service.PedidoService;

@Path("/pedido")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PedidoResource {

    @Inject
    PedidoService service;

    @GET
    @RolesAllowed({ "Admin", "Caixa", "Garcom" })
    public List<PedidoResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "Admin", "Caixa", "Garcom" })
    public Response getId(@PathParam("id") Long id) {
        return service.getId(id);
    }

    @POST
    @RolesAllowed({ "Admin", "Caixa", "Garcom" })
    public Response insert(PedidoDTO pedidoDTO) {
        return service.insert(pedidoDTO);
    }

    @PATCH
    @RolesAllowed({ "Admin", "Caixa", "Garcom" })
    @Path("/delete/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id);
    }

    @PUT
    @RolesAllowed({ "Admin", "Caixa", "Garcom" })
    @Transactional
    public Response removeItemCompra(PedidoRemoveItemCompraDTO pedidoAdicionaRemoveDTO) {
        return service.removeItemCompra(pedidoAdicionaRemoveDTO);
    }

    @PUT
    @RolesAllowed({ "Admin", "Caixa", "Garcom" })
    @Transactional
    public Response adicioaItemCompra(PedidoAdicionaItemCompraDTO pedidoAdicionaRemoveDTO) {
        return service.adicionaItemCompra(pedidoAdicionaRemoveDTO);
    }
}
