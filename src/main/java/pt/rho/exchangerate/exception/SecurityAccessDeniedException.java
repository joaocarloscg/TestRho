package pt.rho.exchangerate.exception;

import org.springframework.http.HttpStatus;

public class SecurityAccessDeniedException extends ApiException {

	private static final long serialVersionUID = 1L;
	private static final HttpStatus STATUS = HttpStatus.FORBIDDEN;

	public SecurityAccessDeniedException(String message) {
		super(STATUS, message);
	}

	public SecurityAccessDeniedException(String message, Throwable cause) {
		super(STATUS, message, cause);
	}
}