package pt.rho.exchangerate.exception;

import org.springframework.http.HttpStatus;

public class SecurityUnauthorizedException extends ApiException {

	private static final long serialVersionUID = 1L;
	private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;

	public SecurityUnauthorizedException(String message) {
		super(STATUS, message);
	}

	public SecurityUnauthorizedException(String message, Throwable cause) {
		super(STATUS, message, cause);
	}
}