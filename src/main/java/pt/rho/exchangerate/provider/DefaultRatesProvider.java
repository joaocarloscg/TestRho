package pt.rho.exchangerate.provider;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import pt.rho.exchangerate.config.ApplicationProperties;
import pt.rho.exchangerate.provider.dto.RatesProviderResponse;
import pt.rho.exchangerate.exception.ExternalProviderException;
import pt.rho.exchangerate.validation.CurrencyValidator;

@Component
public class DefaultRatesProvider implements RatesProvider {

	private final RestClient restClient;
	private final ApplicationProperties applicationProperties;
	private final CurrencyValidator currencyValidator;

	public DefaultRatesProvider(
			RestClient restClient, 
			ApplicationProperties applicationProperties,
			CurrencyValidator currencyValidator) {
		this.restClient = restClient;
		this.applicationProperties = applicationProperties;
		this.currencyValidator = currencyValidator;
	}

	@Override
	public RatesProviderResponse getRates(String baseCurrency) {
		String normalizedBaseCurrency = currencyValidator.validateAndNormalize(baseCurrency);

		RatesProviderResponse response = restClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/live")
						.queryParam("access_key", applicationProperties.getAccessKey())
						.queryParam("source", normalizedBaseCurrency)
						.build())
				.retrieve()
				.body(RatesProviderResponse.class);

		if (response == null) {
			throw new ExternalProviderException("Exchange rate provider returned an empty response");
		}

		return response;
	}
}