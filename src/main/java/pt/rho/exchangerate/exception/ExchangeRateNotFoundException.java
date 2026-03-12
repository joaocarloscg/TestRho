package pt.rho.exchangerate.exception;

import org.springframework.http.HttpStatus;

public class ExchangeRateNotFoundException extends ApiException {

	private static final long serialVersionUID = 1L;
	private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

	public ExchangeRateNotFoundException(String message) {
		super(STATUS, message);
	}

	public ExchangeRateNotFoundException(String message, Throwable cause) {
		super(STATUS, message, cause);
	}
}