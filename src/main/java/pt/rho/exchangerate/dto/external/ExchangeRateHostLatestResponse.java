package pt.rho.exchangerate.dto.external;

import java.math.BigDecimal;
import java.util.Map;

public class ExchangeRateHostLatestResponse {

    private Boolean success;
    private Long timestamp;
    private String source;
    private Map<String, BigDecimal> quotes;
    private ExchangeRateHostError error;

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

    public ExchangeRateHostError getError() {
        return error;
    }

    public void setError(ExchangeRateHostError error) {
        this.error = error;
    }
}