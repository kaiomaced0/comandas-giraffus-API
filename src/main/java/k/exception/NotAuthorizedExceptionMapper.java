package k.exception;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {

    @Override
    public Response toResponse(NotAuthorizedException exception) {
        ProblemDetail pd = new ProblemDetail(
                "about:blank",
                "Unauthorized",
                401,
                exception.getMessage(),
                null);
        return Response.status(401)
                .type(MediaType.APPLICATION_JSON)
                .entity(pd)
                .build();
    }
}
