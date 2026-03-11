package pt.rho.exchangerate.service;

import pt.rho.exchangerate.client.ExchangeRateClient;
import pt.rho.exchangerate.dto.external.ExchangeRateHostLatestResponse;
import pt.rho.exchangerate.exception.ExchangeRateNotFoundException;
import pt.rho.exchangerate.exception.ExternalProviderException;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.validation.CurrencyValidator;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DefaultExchangeRateService implements ExchangeRateService {

    private final ExchangeRateClient exchangeRateClient;
    private final CurrencyValidator currencyValidator;

    public DefaultExchangeRateService(ExchangeRateClient exchangeRateClient,
                                      CurrencyValidator currencyValidator) {
        this.exchangeRateClient = exchangeRateClient;
        this.currencyValidator = currencyValidator;
    }

    @Override
    public ExchangeRates getAllRates(String baseCurrency) {
        String normalizedBaseCurrency = currencyValidator.normalizeAndValidate(baseCurrency);

        ExchangeRateHostLatestResponse response =
                exchangeRateClient.getLatestRates(normalizedBaseCurrency);

        Map<String, BigDecimal> normalizedRates = extractRates(normalizedBaseCurrency, response);

        return new ExchangeRates(normalizedBaseCurrency, normalizedRates);
    }

    @Override
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        String normalizedFromCurrency = currencyValidator.normalizeAndValidate(fromCurrency);
        String normalizedToCurrency = currencyValidator.normalizeAndValidate(toCurrency);

        ExchangeRates exchangeRates = getAllRates(normalizedFromCurrency);

        BigDecimal rate = exchangeRates.getRates().get(normalizedToCurrency);

        if (rate == null) {
            throw new ExchangeRateNotFoundException(
                    "Exchange rate not found from " + normalizedFromCurrency + " to " + normalizedToCurrency);
        }

        return rate;
    }

    private Map<String, BigDecimal> extractRates(String baseCurrency,
                                                 ExchangeRateHostLatestResponse response) {
        if (response == null) {
            throw new ExternalProviderException("No response received from exchange rate provider");
        }

        if (Boolean.FALSE.equals(response.getSuccess())) {
            /*/
        	String errorMessage = response.getError() != null
                    ? response.getError().getInfo()
                    : "External provider returned an unsuccessful response";

            throw new ExternalProviderException(errorMessage);
            /*/
            throw new ExternalProviderException("Failed to fetch exchange rates from external provider");
            //*/
        }

        if (response.getQuotes() == null || response.getQuotes().isEmpty()) {
            throw new ExternalProviderException("No exchange rates returned by external provider");
        }

        Map<String, BigDecimal> extractedRates = response.getQuotes().entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getKey().startsWith(baseCurrency))
                .collect(Collectors.toMap(
                        entry -> entry.getKey().substring(baseCurrency.length()),
                        Map.Entry::getValue
                ));

        if (extractedRates.isEmpty()) {
            throw new ExternalProviderException("No valid exchange rates found for base currency " + baseCurrency);
        }

        return extractedRates;
    }
}