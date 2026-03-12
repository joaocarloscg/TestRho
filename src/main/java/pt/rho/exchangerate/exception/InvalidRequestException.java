package pt.rho.exchangerate.exception;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends ApiException {

	private static final long serialVersionUID = 1L;
	private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

	public InvalidRequestException(String message) {
		super(STATUS, message);
	}

	public InvalidRequestException(String message, Throwable cause) {
		super(STATUS, message, cause);
	}
}