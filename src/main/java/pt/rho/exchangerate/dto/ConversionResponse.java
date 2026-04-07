package pt.rho.exchangerate.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Schema(description = "Conversion response for a single target currency")
@Value
public class ConversionResponse {

	@Schema(description = "Source currency", example = "USD")
	private String from;

	@Schema(description = "Target currency", example = "EUR")
	private String to;

	@Schema(description = "Original amount to convert", example = "100.00")
	private BigDecimal originalAmount;

	@Schema(description = "Exchange rate applied", example = "0.923400")
	private BigDecimal rate;

	@Schema(description = "Converted amount", example = "92.340000")
	private BigDecimal convertedAmount;

}