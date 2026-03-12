package pt.rho.exchangerate.graphql.dto;

import java.math.BigDecimal;

public class CurrencyRateGraphQlResponse {

    private final String currency;
    private final BigDecimal rate;

    public CurrencyRateGraphQlResponse(String currency, BigDecimal rate) {
        this.currency = currency;
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
