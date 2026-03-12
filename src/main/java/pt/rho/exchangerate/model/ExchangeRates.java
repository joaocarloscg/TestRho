package pt.rho.exchangerate.model;

import java.math.BigDecimal;
import java.util.Map;

public class ExchangeRates {

	private String baseCurrency;
	private Map<String, BigDecimal> rates;

	public ExchangeRates(String baseCurrency, Map<String, BigDecimal> rates) {
		this.baseCurrency = baseCurrency;
		this.rates = rates;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public Map<String, BigDecimal> getRates() {
		return rates;
	}

	public void setRates(Map<String, BigDecimal> rates) {
		this.rates = rates;
	}
}