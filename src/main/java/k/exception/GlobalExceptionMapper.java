package k.exception;

import org.jboss.logging.Logger;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        LOG.error("Erro nao tratado", exception);
        ProblemDetails problem = new ProblemDetails(
                "about:blank",
                "Internal Server Error",
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "Ocorreu um erro inesperado ao processar a requisicao.",
                uriInfo != null ? uriInfo.getPath() : null);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(problem)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
