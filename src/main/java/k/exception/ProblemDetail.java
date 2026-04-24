package k.exception;

import java.util.List;

/**
 * RFC 7807 - Problem Details for HTTP APIs.
 */
public record ProblemDetail(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        List<Violation> violations) {

    public ProblemDetail(String type, String title, int status, String detail, String instance) {
        this(type, title, status, detail, instance, null);
    }

    public record Violation(String field, String message) {
    }
}
