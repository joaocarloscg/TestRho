package pt.rho.exchangerate.service;

import pt.rho.exchangerate.client.ExchangeRateClient;
import pt.rho.exchangerate.dto.external.ExchangeRateHostLatestResponse;
import pt.rho.exchangerate.exception.ExchangeRateNotFoundException;
import pt.rho.exchangerate.exception.ExternalProviderException;
import pt.rho.exchangerate.exception.InvalidAmountException;
import pt.rho.exchangerate.exception.InvalidRequestException;
import pt.rho.exchangerate.model.ConversionResult;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.model.MultiConversionResult;
import pt.rho.exchangerate.validation.CurrencyValidator;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
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

    @Override
    public ConversionResult convert(String fromCurrency, String toCurrency, BigDecimal amount) {
        validateAmount(amount);

        String normalizedFromCurrency = currencyValidator.normalizeAndValidate(fromCurrency);
        String normalizedToCurrency = currencyValidator.normalizeAndValidate(toCurrency);

        BigDecimal rate = getExchangeRate(normalizedFromCurrency, normalizedToCurrency);
        BigDecimal convertedAmount = calculateConvertedAmount(amount, rate);

        return new ConversionResult(
                normalizedFromCurrency,
                normalizedToCurrency,
                amount,
                rate,
                convertedAmount
        );
    }
    
    @Override
    public MultiConversionResult convert(String fromCurrency, List<String> toCurrencies, BigDecimal amount) {
        validateAmount(amount);

        String normalizedFromCurrency = currencyValidator.normalizeAndValidate(fromCurrency);
        
        if (toCurrencies == null || toCurrencies.isEmpty()) {
            throw new InvalidRequestException("Target currencies must not be empty");
        }

        ExchangeRates exchangeRates = getAllRates(normalizedFromCurrency);

        List<ConversionResult> conversions = toCurrencies.stream()
                .map(currencyValidator::normalizeAndValidate)
                .distinct()
                .map(toCurrency -> {
                    BigDecimal rate = exchangeRates.getRates().get(toCurrency);

                    if (rate == null) {
                        throw new ExchangeRateNotFoundException(
                                "Exchange rate not found from " + normalizedFromCurrency + " to " + toCurrency);
                    }

                    return new ConversionResult(
                            normalizedFromCurrency,
                            toCurrency,
                            amount,
                            rate,
                            calculateConvertedAmount(amount, rate)
                    );
                })
                .toList();

        return new MultiConversionResult(normalizedFromCurrency, amount, conversions);
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

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAmountException("Amount must not be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Amount must not be negative");
        }
    }

    private BigDecimal calculateConvertedAmount(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate).setScale(6, RoundingMode.HALF_UP);
    }
}