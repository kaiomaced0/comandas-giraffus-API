package k.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof jakarta.ws.rs.WebApplicationException wae) {
            return wae.getResponse();
        }
        if (exception instanceof BusinessException business) {
            ProblemDetail pd = new ProblemDetail(
                    "about:blank",
                    "Business rule violation",
                    business.getStatus().getStatusCode(),
                    business.getMessage(),
                    null);
            return Response.status(business.getStatus())
                    .type(MediaType.APPLICATION_JSON)
                    .entity(pd)
                    .build();
        }
        LOG.error("Erro inesperado", exception);
        ProblemDetail pd = new ProblemDetail(
                "about:blank",
                "Internal Server Error",
                500,
                exception.getMessage() == null ? "Erro inesperado" : exception.getMessage(),
                null);
        return Response.status(500)
                .type(MediaType.APPLICATION_JSON)
                .entity(pd)
                .build();
    }
}
