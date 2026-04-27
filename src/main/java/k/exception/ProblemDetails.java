package k.exception;

public record ProblemDetails(
                String type,
                String title,
                int status,
                String detail,
                String instance) {
}
