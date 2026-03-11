package pt.rho.exchangerate.service;

import pt.rho.exchangerate.model.ExchangeRates;

import java.math.BigDecimal;

public interface ExchangeRateService {

    ExchangeRates getAllRates(String baseCurrency);

    BigDecimal getExchangeRate(String fromCurrency, String toCurrency);
}