package pt.rho.exchangerate.mapper;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import pt.rho.exchangerate.provider.dto.RatesProviderResponse;
import pt.rho.exchangerate.exception.ExternalProviderException;
import pt.rho.exchangerate.model.ExchangeRates;

@Component
public class RatesProviderResponseMapper {

	public ExchangeRates toExchangeRates(String baseCurrency, RatesProviderResponse response) {
		validateResponse(baseCurrency, response);

		Map<String, BigDecimal> extractedRates = response.getQuotes().entrySet().stream()
				.filter(entry -> entry.getKey() != null)
				.filter(entry -> entry.getKey().length() == 6)
				.filter(entry -> entry.getKey().startsWith(baseCurrency))
				.collect(Collectors.toMap(entry -> entry.getKey()
						.substring(baseCurrency.length()), Map.Entry::getValue));

		if (extractedRates.isEmpty()) {
			throw new ExternalProviderException("No valid exchange rates found for base currency " + baseCurrency);
		}

		return new ExchangeRates(baseCurrency, extractedRates);
	}

	private void validateResponse(String baseCurrency, RatesProviderResponse response) {
		if (response == null) {
			throw new ExternalProviderException("No response received from exchange rate provider");
		}

		if (Boolean.FALSE.equals(response.getSuccess())) {
			throw new ExternalProviderException("Failed to fetch exchange rates from external provider");
		}

		if (response.getQuotes() == null || response.getQuotes().isEmpty()) {
			throw new ExternalProviderException(
					"No exchange rates returned by external provider for base currency " + baseCurrency);
		}
	}
}