package pt.rho.exchangerate.dto;

import java.math.BigDecimal;
import java.util.Map;

public class ExchangeRatesResponse {

    private String base;
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