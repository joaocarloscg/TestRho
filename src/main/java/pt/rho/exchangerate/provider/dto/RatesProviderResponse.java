package pt.rho.exchangerate.provider.dto;

import java.math.BigDecimal;
import java.util.Map;

public class RatesProviderResponse {

	private Boolean success;
	private Long timestamp;
	private String source;
	private Map<String, BigDecimal> quotes;
	private RatesProviderResponseError error;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Map<String, BigDecimal> getQuotes() {
		return quotes;
	}

	public void setQuotes(Map<String, BigDecimal> quotes) {
		this.quotes = quotes;
	}

	public RatesProviderResponseError getError() {
		return error;
	}

	public void setError(RatesProviderResponseError error) {
		this.error = error;
	}
}