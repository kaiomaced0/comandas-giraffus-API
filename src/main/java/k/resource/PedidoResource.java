package k.resource;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.PagedResponse;
import k.dto.PedidoAdicionaItemCompraDTO;
import k.dto.PedidoDTO;
import k.dto.PedidoRemoveItemCompraDTO;
import k.dto.PedidoResponseDTO;
import k.dto.PedidoStatusInputDTO;
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

    /**
     * Listagem paginada de pedidos com filtros. Endpoint dedicado (não conflita
     * com {@link #getAll()}) — preserva clientes existentes.
     */
    @GET
    @Path("/page")
    @RolesAllowed({ "Admin", "Caixa", "Garcom", "Cozinha" })
    public PagedResponse<PedidoResponseDTO> listPaged(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("status") String status,
            @QueryParam("comandaId") Long comandaId,
            @QueryParam("from") String from,
            @QueryParam("to") String to) {
        LocalDate fromDate = parseIsoDate("from", from);
        LocalDate toDate = parseIsoDate("to", to);
        return service.list(status, comandaId, fromDate, toDate, page, size);
    }

    @PATCH
    @Path("/{id}/status")
    @RolesAllowed({ "Master", "Admin", "Garcom", "Cozinha" })
    public PedidoResponseDTO atualizarStatus(@PathParam("id") Long id, PedidoStatusInputDTO dto) {
        return service.atualizarStatus(id, dto);
    }

    private static LocalDate parseIsoDate(String paramName, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new WebApplicationException(
                    "Parametro '" + paramName + "' invalido: esperado ISO date (YYYY-MM-DD)",
                    Response.Status.BAD_REQUEST);
        }
    }
}
