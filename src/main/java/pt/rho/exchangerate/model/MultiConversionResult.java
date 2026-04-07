package pt.rho.exchangerate.model;

import java.math.BigDecimal;
import java.util.List;

import lombok.Value;

@Value
public class MultiConversionResult {

	private String fromCurrency;
	private BigDecimal originalAmount;
	private List<ConversionResult> conversions;

}