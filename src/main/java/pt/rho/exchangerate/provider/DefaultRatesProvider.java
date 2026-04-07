package pt.rho.exchangerate.provider;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import pt.rho.exchangerate.config.ApplicationProperties;
import pt.rho.exchangerate.provider.dto.RatesProviderResponse;
import pt.rho.exchangerate.web.validation.CurrencyCode;
import pt.rho.exchangerate.exception.ExternalProviderException;

@Component
@RequiredArgsConstructor
public class DefaultRatesProvider implements RatesProvider {

	private final RestClient restClient;
	private final ApplicationProperties applicationProperties;

	@Override
	public RatesProviderResponse getRates(@CurrencyCode String baseCurrency) {
		RatesProviderResponse response = restClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/live")
						.queryParam("access_key", applicationProperties.getAccessKey())
						.queryParam("source", baseCurrency)
						.build())
				.retrieve()
				.body(RatesProviderResponse.class);

		if (response == null) {
			throw new ExternalProviderException("Exchange rate provider returned an empty response");
		}

		return response;
	}
}