package pt.rho.exchangerate.dto;

import java.math.BigDecimal;

import lombok.Value;

@Value
public class CurrencyRateGraphQlResponse {

    private final String currency;
    private final BigDecimal rate;
}
