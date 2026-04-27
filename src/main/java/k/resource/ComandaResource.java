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
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.ComandaDTO;
import k.dto.ComandaPagedItemDTO;
import k.dto.ComandaResponseDTO;
import k.dto.DocumentoFiscalResponseDTO;
import k.dto.FiscalEmissaoComandaInputDTO;
import k.dto.PagamentoMultiInputDTO;
import k.dto.PagamentoMultiResponseDTO;
import k.dto.PagedResponse;
import k.service.ComandaService;
import k.service.FiscalService;
import k.service.PagamentoService;

@Path("/comanda")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ComandaResource {

    @Inject
    ComandaService service;

    @Inject
    PagamentoService pagamentoService;

    @Inject
    FiscalService fiscalService;

    @GET
    @RolesAllowed({ "Admin", "Caixa", "Garcom", "Cozinha" })
    public List<ComandaResponseDTO> getAll() {
        return service.getAll();
    }

    /**
     * Listagem paginada de comandas com filtros. Endpoint dedicado (não conflita
     * com {@link #getAll()}) — preserva clientes existentes.
     */
    @GET
    @Path("/page")
    @RolesAllowed({ "Admin", "Caixa", "Garcom", "Cozinha" })
    public PagedResponse<ComandaPagedItemDTO> listPaged(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("mesaId") Long mesaId,
            @QueryParam("finalizada") Boolean finalizada,
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("atendenteId") Long atendenteId) {
        LocalDate fromDate = parseIsoDate("from", from);
        LocalDate toDate = parseIsoDate("to", to);
        return service.list(mesaId, finalizada, fromDate, toDate, atendenteId, page, size);
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
    public List<ComandaResponseDTO> getNome(@PathParam("nome") String nome) {
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

    // ===================== Onda F - atalhos REST de pagamentos =====================

    @GET
    @Path("/{id}/pagamentos")
    @RolesAllowed({ "Master", "Admin", "Caixa", "Garcom" })
    public List<PagamentoMultiResponseDTO> listarPagamentos(@PathParam("id") Long id) {
        return pagamentoService.listarPorComanda(id);
    }

    @POST
    @Path("/{id}/pagamentos")
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    public Response criarPagamento(@PathParam("id") Long id, PagamentoMultiInputDTO dto) {
        PagamentoMultiResponseDTO created = pagamentoService.insert(id, dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    // ===================== Onda G - atalho REST de documento fiscal =====================

    @POST
    @Path("/{id}/fiscal")
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    public Response emitirFiscal(@PathParam("id") Long id, FiscalEmissaoComandaInputDTO dto) {
        DocumentoFiscalResponseDTO created = fiscalService.emitirSobreComanda(id, dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}
