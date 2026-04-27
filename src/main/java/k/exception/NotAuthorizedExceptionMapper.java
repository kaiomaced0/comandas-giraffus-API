package k.exception;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(NotAuthorizedException exception) {
        ProblemDetails problem = new ProblemDetails(
                "about:blank",
                "Unauthorized",
                Response.Status.UNAUTHORIZED.getStatusCode(),
                exception.getMessage(),
                uriInfo != null ? uriInfo.getPath() : null);
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(problem)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
