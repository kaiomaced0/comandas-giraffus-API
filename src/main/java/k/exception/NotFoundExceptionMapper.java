package k.exception;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        ProblemDetail pd = new ProblemDetail(
                "about:blank",
                "Not Found",
                404,
                exception.getMessage(),
                null);
        return Response.status(404)
                .type(MediaType.APPLICATION_JSON)
                .entity(pd)
                .build();
    }
}
