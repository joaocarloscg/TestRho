package pt.rho.exchangerate.dto;

import java.util.List;

import lombok.Value;

@Value
public class ExchangeRatesGraphQlResponse {

    private final String base;
    private final List<CurrencyRateGraphQlResponse> rates;

}
