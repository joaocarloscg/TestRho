package pt.rho.exchangerate.service;

import java.math.BigDecimal;
import java.util.List;

import pt.rho.exchangerate.model.ConversionResult;
import pt.rho.exchangerate.model.ExchangeRateResult;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.model.MultiConversionResult;

public interface ExchangeRateService {

	ExchangeRates getAllRates(String baseCurrency);

	ExchangeRateResult getExchangeRate(String fromCurrency, String toCurrency);

	ConversionResult convert(String fromCurrency, String toCurrency, BigDecimal amount);

	MultiConversionResult convert(String fromCurrency, List<String> toCurrencies, BigDecimal amount);
}