package pt.rho.exchangerate.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Schema(description = "Single conversion item in a multi-conversion response")
@Value
public class SingleConversionItemResponse {

	@Schema(description = "Target currency", example = "EUR")
	private String currency;

	@Schema(description = "Exchange rate applied", example = "0.923400")
	private BigDecimal rate;

	@Schema(description = "Converted amount", example = "92.340000")
	private BigDecimal convertedAmount;

}