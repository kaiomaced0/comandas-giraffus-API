package k.exception;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<ProblemDetail.Violation> violations = exception.getConstraintViolations().stream()
                .map(v -> new ProblemDetail.Violation(v.getPropertyPath().toString(), v.getMessage()))
                .collect(Collectors.toList());

        ProblemDetail pd = new ProblemDetail(
                "about:blank",
                "Validation Failed",
                400,
                "Um ou mais campos falharam validação",
                null,
                violations);

        return Response.status(400)
                .type(MediaType.APPLICATION_JSON)
                .entity(pd)
                .build();
    }
}
