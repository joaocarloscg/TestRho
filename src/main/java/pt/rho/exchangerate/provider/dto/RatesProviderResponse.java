package pt.rho.exchangerate.provider.dto;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Data;

@Data
public class RatesProviderResponse {

	private Boolean success;
	private Long timestamp;
	private String source;
	private Map<String, BigDecimal> quotes;
	private RatesProviderResponseError error;

}