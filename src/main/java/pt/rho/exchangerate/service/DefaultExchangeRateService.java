package pt.rho.exchangerate.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pt.rho.exchangerate.exception.ExchangeRateNotFoundException;
import pt.rho.exchangerate.exception.InvalidAmountException;
import pt.rho.exchangerate.exception.InvalidRequestException;
import pt.rho.exchangerate.model.ConversionResult;
import pt.rho.exchangerate.model.ExchangeRateResult;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.model.MultiConversionResult;
import pt.rho.exchangerate.validation.CurrencyValidator;

@Service
@RequiredArgsConstructor
public class DefaultExchangeRateService implements ExchangeRateService {

	private final CachedProviderService cachedProviderService;
	private final CurrencyValidator currencyValidator;

	@Override
	public ExchangeRates getAllRates(String baseCurrency) {
		String normalizedBaseCurrency = currencyValidator.validateAndNormalize(baseCurrency);

		return cachedProviderService.getAllRates(normalizedBaseCurrency);
	}

	@Override
	public ExchangeRateResult getExchangeRate(String fromCurrency, String toCurrency) {
		String normalizedFromCurrency = currencyValidator.validateAndNormalize(fromCurrency);
		String normalizedToCurrency = currencyValidator.validateAndNormalize(toCurrency);

		ExchangeRates exchangeRates = cachedProviderService.getAllRates(normalizedFromCurrency);

		BigDecimal rate = exchangeRates.getRates().get(normalizedToCurrency);

		if (rate == null) {
			throw new ExchangeRateNotFoundException(
					"Exchange rate not found from " + normalizedFromCurrency + " to " + normalizedToCurrency);
		}

		return new ExchangeRateResult(normalizedFromCurrency, normalizedToCurrency, rate);
	}

	@Override
	public ConversionResult convert(String fromCurrency, String toCurrency, BigDecimal amount) {
		validateConversionAmount(amount);

		String normalizedFromCurrency = currencyValidator.validateAndNormalize(fromCurrency);
		String normalizedToCurrency = currencyValidator.validateAndNormalize(toCurrency);

		ExchangeRates exchangeRates = cachedProviderService.getAllRates(normalizedFromCurrency);

		BigDecimal rate = exchangeRates.getRates().get(normalizedToCurrency);

		if (rate == null) {
			throw new ExchangeRateNotFoundException(
					"Exchange rate not found from " + normalizedFromCurrency + " to " + normalizedToCurrency);
		}

		BigDecimal convertedAmount = calculateConvertedAmount(amount, rate);

		return new ConversionResult(normalizedFromCurrency, normalizedToCurrency, amount, rate, convertedAmount);
	}

	@Override
	public MultiConversionResult convert(String fromCurrency, List<String> toCurrencies, BigDecimal amount) {
		validateConversionAmount(amount);

		String normalizedFromCurrency = currencyValidator.validateAndNormalize(fromCurrency);

		if (toCurrencies == null || toCurrencies.isEmpty()) {
			throw new InvalidRequestException("Target currencies must not be empty");
		}

		ExchangeRates exchangeRates = cachedProviderService.getAllRates(normalizedFromCurrency);

		List<ConversionResult> conversions = toCurrencies.stream().map(currencyValidator::validateAndNormalize)
				.distinct().map(toCurrency -> {
					BigDecimal rate = exchangeRates.getRates().get(toCurrency);

					if (rate == null) {
						throw new ExchangeRateNotFoundException(
								"Exchange rate not found from " + normalizedFromCurrency + " to " + toCurrency);
					}

					return new ConversionResult(normalizedFromCurrency, toCurrency, amount, rate,
							calculateConvertedAmount(amount, rate));
				}).toList();

		return new MultiConversionResult(normalizedFromCurrency, amount, conversions);
	}

	private void validateConversionAmount(BigDecimal amount) {
		if (amount == null) {
			throw new InvalidAmountException("Amount must not be null");
		}

		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidAmountException("Amount must be positive");
		}
	}

	private BigDecimal calculateConvertedAmount(BigDecimal amount, BigDecimal rate) {
		return amount.multiply(rate).setScale(6, RoundingMode.HALF_UP);
	}
}