package pt.rho.exchangerate.client;

import pt.rho.exchangerate.dto.external.ExchangeRateHostLatestResponse;

public interface ExchangeRateClient {

    ExchangeRateHostLatestResponse getLatestRates(String baseCurrency);
}