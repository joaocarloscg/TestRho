package pt.rho.exchangerate.model;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class ExchangeRateResult {

	private String fromCurrency;
	private String toCurrency;
	private BigDecimal rate;

}