package pt.rho.exchangerate.exception;

import org.springframework.http.HttpStatus;

public class InvalidCurrencyException extends ApiException {

	private static final long serialVersionUID = 1L;
	private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

	public InvalidCurrencyException(String message) {
		super(STATUS, message);
	}

	public InvalidCurrencyException(String message, Throwable cause) {
		super(STATUS, message, cause);
	}
}