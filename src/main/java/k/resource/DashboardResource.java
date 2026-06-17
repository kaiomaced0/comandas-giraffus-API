package k.resource;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.DashboardKpisDTO;
import k.service.DashboardService;

@Path("/dashboard")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

    @Inject
    DashboardService service;

    @GET
    @Path("/kpis")
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    public DashboardKpisDTO kpis(
            @QueryParam("from") String from,
            @QueryParam("to") String to) {
        LocalDate fromDate = parseIsoDate("from", from);
        LocalDate toDate = parseIsoDate("to", to);
        return service.kpis(fromDate, toDate);
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
