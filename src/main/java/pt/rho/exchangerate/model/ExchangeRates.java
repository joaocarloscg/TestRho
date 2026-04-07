package pt.rho.exchangerate.model;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Value;

@Value
public class ExchangeRates {

	private String baseCurrency;
	private Map<String, BigDecimal> rates;

}