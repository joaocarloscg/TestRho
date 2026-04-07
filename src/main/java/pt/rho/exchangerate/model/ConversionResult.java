package pt.rho.exchangerate.model;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class ConversionResult {

	private String fromCurrency;
	private String toCurrency;
	private BigDecimal originalAmount;
	private BigDecimal rate;
	private BigDecimal convertedAmount;

}