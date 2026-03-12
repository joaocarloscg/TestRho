package pt.rho.exchangerate.dto;

import java.math.BigDecimal;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Exchange rate response from one to multiple currencies")
public class ExchangeRatesResponse {

	@Schema(description = "Source currency", example = "USD")
	private String base;
	@Schema(description = "List of conversion results")
	private Map<String, BigDecimal> rates;

	public ExchangeRatesResponse(String base, Map<String, BigDecimal> rates) {
		this.base = base;
		this.rates = rates;
	}

	public String getBase() {
		return base;
	}

	public Map<String, BigDecimal> getRates() {
		return rates;
	}
}