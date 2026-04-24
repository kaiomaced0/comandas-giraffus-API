package k.exception;

import jakarta.ws.rs.core.Response.Status;

/**
 * Exception for domain/business rule violations. Mapped to RFC 7807 Problem Details.
 */
public class BusinessException extends RuntimeException {

    private final Status status;

    public BusinessException(String message) {
        this(Status.BAD_REQUEST, message);
    }

    public BusinessException(Status status, String message) {
        super(message);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
