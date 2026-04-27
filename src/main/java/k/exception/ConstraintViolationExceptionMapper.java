package k.exception;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<String> violacoes = exception.getConstraintViolations().stream()
                .map(this::formatViolation)
                .collect(Collectors.toList());

        String detail = "Erros de validacao: " + String.join("; ", violacoes);

        ProblemDetails problem = new ProblemDetails(
                "about:blank",
                "Unprocessable Entity",
                422,
                detail,
                uriInfo != null ? uriInfo.getPath() : null);
        return Response.status(422)
                .entity(problem)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private String formatViolation(ConstraintViolation<?> violation) {
        return violation.getPropertyPath() + ": " + violation.getMessage();
    }
}
