package pt.rho.exchangerate.dto;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Schema(description = "Standard API error response")
@Value
public class ErrorResponse {

	@Schema(description = "Timestamp of the error", example = "2026-03-11T15:10:00Z")
	private OffsetDateTime timestamp;

	@Schema(description = "HTTP status code", example = "400")
	private int status;

	@Schema(description = "HTTP error name", example = "Bad Request")
	private String error;

	@Schema(description = "Error message", example = "Currency must have exactly 3 characters")
	private String message;

	@Schema(description = "Request path", example = "/api/v1/rates/EU/USD")
	private String path;

}