package pt.rho.exchangerate.service;

import pt.rho.exchangerate.model.ConversionResult;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.model.MultiConversionResult;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeRateService {

    ExchangeRates getAllRates(String baseCurrency);

    BigDecimal getExchangeRate(String fromCurrency, String toCurrency);

    ConversionResult convert(String fromCurrency, String toCurrency, BigDecimal amount);

    MultiConversionResult convert(String fromCurrency, List<String> toCurrencies, BigDecimal amount);
}