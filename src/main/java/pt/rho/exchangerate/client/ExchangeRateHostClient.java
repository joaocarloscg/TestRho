package pt.rho.exchangerate.client;

import pt.rho.exchangerate.dto.external.ExchangeRateHostLatestResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ExchangeRateHostClient implements ExchangeRateClient {

    private final RestClient restClient;

    public ExchangeRateHostClient(RestClient exchangeRateRestClient) {
        this.restClient = exchangeRateRestClient;
    }

    @Override
    public ExchangeRateHostLatestResponse getLatestRates(String baseCurrency) {
        String normalizedBaseCurrency = baseCurrency.toUpperCase();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/latest")
                        .queryParam("base", normalizedBaseCurrency)
                        .build())
                .retrieve()
                .body(ExchangeRateHostLatestResponse.class);
    }
}