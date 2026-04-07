package pt.rho.exchangerate.dto;

import java.math.BigDecimal;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Schema(description = "Exchange rate response from one to multiple currencies")
@Value
public class ExchangeRatesResponse {

	@Schema(description = "Source currency", example = "USD")
	private String base;
	@Schema(description = "List of conversion results")
	private Map<String, BigDecimal> rates;

}