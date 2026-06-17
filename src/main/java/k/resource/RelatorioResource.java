package k.resource;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.FaturamentoPorFormaDTO;
import k.dto.TopProdutoDTO;
import k.dto.VendaPorDiaDTO;
import k.service.RelatorioService;

@Path("/relatorio")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RelatorioResource {

    @Inject
    RelatorioService service;

    @GET
    @Path("/forma-pagamento")
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    public List<FaturamentoPorFormaDTO> formaPagamento(
            @QueryParam("from") String from,
            @QueryParam("to") String to) {
        LocalDate fromDate = parseIsoDate("from", from);
        LocalDate toDate = parseIsoDate("to", to);
        return service.faturamentoPorForma(fromDate, toDate);
    }

    @GET
    @Path("/vendas-por-dia")
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    public List<VendaPorDiaDTO> vendasPorDia(
            @QueryParam("from") String from,
            @QueryParam("to") String to) {
        LocalDate fromDate = parseIsoDate("from", from);
        LocalDate toDate = parseIsoDate("to", to);
        return service.vendasPorDia(fromDate, toDate);
    }

    @GET
    @Path("/top-produtos")
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    public List<TopProdutoDTO> topProdutos(
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("limit") @DefaultValue("10") int limit) {
        LocalDate fromDate = parseIsoDate("from", from);
        LocalDate toDate = parseIsoDate("to", to);
        return service.topProdutos(fromDate, toDate, limit);
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
