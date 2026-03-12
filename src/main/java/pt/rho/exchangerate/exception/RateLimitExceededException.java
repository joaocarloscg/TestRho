package pt.rho.exchangerate.exception;

import org.springframework.http.HttpStatus;

public class RateLimitExceededException extends ApiException {

	private static final long serialVersionUID = 1L;
	private static final HttpStatus STATUS = HttpStatus.TOO_MANY_REQUESTS;

	public RateLimitExceededException(String message) {
		super(STATUS, message);
	}

	public RateLimitExceededException(String message, Throwable cause) {
		super(STATUS, message, cause);
	}
}