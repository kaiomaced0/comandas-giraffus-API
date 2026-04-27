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
import k.dto.DocumentoFiscalResponseDTO;
import k.dto.FiscalEmissaoPagamentoInputDTO;
import k.dto.PagamentoDTO;
import k.dto.PagamentoDeleteDTO;
import k.dto.PagamentoEstornarInputDTO;
import k.dto.PagamentoMultiInputDTO;
import k.dto.PagamentoMultiResponseDTO;
import k.dto.PagamentoResponseDTO;
import k.dto.PagedResponse;
import k.service.FiscalService;
import k.service.PagamentoService;

@Path("/pagamento")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PagamentoResource {

    @Inject
    PagamentoService service;

    @Inject
    FiscalService fiscalService;

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

    // ===================== Onda F - pagamentos múltiplos =====================

    @POST
    @Path("/comanda/{comandaId}")
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    public Response insertMulti(@PathParam("comandaId") Long comandaId, PagamentoMultiInputDTO dto) {
        PagamentoMultiResponseDTO created = service.insert(comandaId, dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/comanda/{comandaId}")
    @RolesAllowed({ "Master", "Admin", "Caixa", "Garcom" })
    public List<PagamentoMultiResponseDTO> listarPorComanda(@PathParam("comandaId") Long comandaId) {
        return service.listarPorComanda(comandaId);
    }

    @PATCH
    @Path("/{id}/estornar")
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    public PagamentoMultiResponseDTO estornar(@PathParam("id") Long id, PagamentoEstornarInputDTO dto) {
        return service.estornar(id, dto);
    }

    // ===================== Onda G - atalho REST de documento fiscal =====================

    @POST
    @Path("/{id}/fiscal")
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    public Response emitirFiscal(@PathParam("id") Long id, FiscalEmissaoPagamentoInputDTO dto) {
        DocumentoFiscalResponseDTO created = fiscalService.emitirSobrePagamento(id, dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Listagem paginada de pagamentos com filtros. Endpoint dedicado (não conflita
     * com {@link #getAll()}) — preserva clientes existentes.
     */
    @GET
    @Path("/page")
    @RolesAllowed({ "Admin", "Caixa" })
    public PagedResponse<PagamentoResponseDTO> listPaged(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("caixaId") Long caixaId,
            @QueryParam("formaPagamento") Integer formaPagamento,
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("usuarioId") Long usuarioId) {
        LocalDate fromDate = parseIsoDate("from", from);
        LocalDate toDate = parseIsoDate("to", to);
        return service.list(caixaId, formaPagamento, fromDate, toDate, usuarioId, page, size);
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
