package pt.rho.exchangerate.graphql.dto;

import java.util.List;

public class ExchangeRatesGraphQlResponse {

    private final String base;
    private final List<CurrencyRateGraphQlResponse> rates;

    public ExchangeRatesGraphQlResponse(String base, List<CurrencyRateGraphQlResponse> rates) {
        this.base = base;
        this.rates = rates;
    }

    public String getBase() {
        return base;
    }

    public List<CurrencyRateGraphQlResponse> getRates() {
        return rates;
    }
}
