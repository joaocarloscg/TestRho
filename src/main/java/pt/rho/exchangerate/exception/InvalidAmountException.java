package pt.rho.exchangerate.exception;

import org.springframework.http.HttpStatus;

public class InvalidAmountException extends ApiException {

	private static final long serialVersionUID = 1L;
	private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

	public InvalidAmountException(String message) {
		super(STATUS, message);
	}

	public InvalidAmountException(String message, Throwable cause) {
		super(STATUS, message, cause);
	}
}