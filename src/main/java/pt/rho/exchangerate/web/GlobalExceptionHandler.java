package pt.rho.exchangerate.web;

import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import pt.rho.exchangerate.exception.ApiException;
import pt.rho.exchangerate.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {

		return buildErrorResponse(ex.getHttpStatus(), ex.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
			MissingServletRequestParameterException ex, HttpServletRequest request) {

		String message = "Required request parameter '%s' is missing".formatted(ex.getParameterName());

		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

		String message = "Invalid value for parameter '%s': %s".formatted(ex.getName(), ex.getValue());

		return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
	}

	@ExceptionHandler(RestClientException.class)
	public ResponseEntity<ErrorResponse> handleRestClientException(Exception ex, HttpServletRequest request) {

		log.warn("External provider request failed for [{} {}]", request.getMethod(), request.getRequestURI(), ex);

		return buildErrorResponse(HttpStatus.BAD_GATEWAY,
				"Failed to retrieve data from external exchange rate provider", request.getRequestURI());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {

		log.error("Unexpected error while processing request [{} {}]", request.getMethod(), request.getRequestURI(),
				ex);

		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred",
				request.getRequestURI());
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String path) {

		ErrorResponse response = new ErrorResponse(OffsetDateTime.now(), status.value(), status.getReasonPhrase(),
				message, path);

		return ResponseEntity.status(status).body(response);
	}
}