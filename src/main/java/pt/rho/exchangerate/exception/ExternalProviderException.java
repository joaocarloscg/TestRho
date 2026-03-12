package pt.rho.exchangerate.exception;

import org.springframework.http.HttpStatus;

public class ExternalProviderException extends ApiException {

	private static final long serialVersionUID = 1L;
	private static final HttpStatus STATUS = HttpStatus.BAD_GATEWAY;

	public ExternalProviderException(String message) {
		super(STATUS, message);
	}

	public ExternalProviderException(String message, Throwable cause) {
		super(STATUS, message, cause);
	}
}