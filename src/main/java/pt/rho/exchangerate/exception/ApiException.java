package pt.rho.exchangerate.exception;

import java.util.Objects;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final HttpStatus httpStatus;
    
    protected ApiException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = Objects.requireNonNull(httpStatus, "httpStatus must not be null");
    }

    protected ApiException(HttpStatus httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = Objects.requireNonNull(httpStatus, "httpStatus must not be null");
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}