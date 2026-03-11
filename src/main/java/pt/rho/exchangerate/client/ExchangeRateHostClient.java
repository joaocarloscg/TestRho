package pt.rho.exchangerate.client;

import pt.rho.exchangerate.config.ExchangeRateProviderProperties;
import pt.rho.exchangerate.dto.external.ExchangeRateHostLatestResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ExchangeRateHostClient implements ExchangeRateClient {

    private final RestClient restClient;
    private final ExchangeRateProviderProperties properties;

    public ExchangeRateHostClient(RestClient exchangeRateRestClient,
                                  ExchangeRateProviderProperties properties) {
        this.restClient = exchangeRateRestClient;
        this.properties = properties;
    }

    @Override
    public ExchangeRateHostLatestResponse getLatestRates(String baseCurrency) {
        String normalizedBaseCurrency = baseCurrency.toUpperCase();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/live")
                        .queryParam("access_key", properties.getAccessKey())
                        .queryParam("source", normalizedBaseCurrency)
                        .build())
                .retrieve()
                .body(ExchangeRateHostLatestResponse.class);
    }
}