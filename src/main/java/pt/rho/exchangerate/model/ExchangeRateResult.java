package pt.rho.exchangerate.model;

import java.math.BigDecimal;

public class ExchangeRateResult {

	private String fromCurrency;
	private String toCurrency;
	private BigDecimal rate;

	public ExchangeRateResult(String fromCurrency, String toCurrency, BigDecimal rate) {
		this.fromCurrency = fromCurrency;
		this.toCurrency = toCurrency;
		this.rate = rate;
	}

	public String getFromCurrency() {
		return fromCurrency;
	}

	public String getToCurrency() {
		return toCurrency;
	}

	public BigDecimal getRate() {
		return rate;
	}
}