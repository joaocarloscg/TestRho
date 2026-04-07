package pt.rho.exchangerate.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Schema(description = "Exchange rate response between two currencies")
@Value
public class ExchangeRateResponse {

	@Schema(description = "Source currency", example = "USD")
	private String from;

	@Schema(description = "Target currency", example = "EUR")
	private String to;

	@Schema(description = "Exchange rate from source to target currency", example = "0.9234")
	private BigDecimal rate;

}