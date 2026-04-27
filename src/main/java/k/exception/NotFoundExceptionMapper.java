package k.exception;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(NotFoundException exception) {
        ProblemDetails problem = new ProblemDetails(
                "about:blank",
                "Not Found",
                Response.Status.NOT_FOUND.getStatusCode(),
                exception.getMessage(),
                uriInfo != null ? uriInfo.getPath() : null);
        return Response.status(Response.Status.NOT_FOUND)
                .entity(problem)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
