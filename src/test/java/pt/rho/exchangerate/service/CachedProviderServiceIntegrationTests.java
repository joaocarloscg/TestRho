package pt.rho.exchangerate.service;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import pt.rho.exchangerate.provider.RatesProvider;
import pt.rho.exchangerate.provider.dto.RatesProviderResponse;

@SpringBootTest
class CachedProviderServiceIntegrationTests {

    private static final String EXCHANGE_RATES_CACHE = "exchangeRates";

    @Autowired
    private CachedProviderService cachedProviderService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private RatesProvider ratesProvider;

    @BeforeEach
    void setUp() {
        if (cacheManager.getCache(EXCHANGE_RATES_CACHE) != null) {
            cacheManager.getCache(EXCHANGE_RATES_CACHE).clear();
        }
        reset(ratesProvider);
    }

    @Test
    @DisplayName("Should use cache for repeated requests with the same normalized base currency")
    void shouldUseCacheForRepeatedNormalizedRequests() {
        RatesProviderResponse response = buildSuccessfulResponse(
                "USD",
                Map.of("USDEUR", new BigDecimal("0.92"))
        );

        when(ratesProvider.getRates("USD")).thenReturn(response);

        cachedProviderService.getAllRates("USD");
        cachedProviderService.getAllRates("USD");

        verify(ratesProvider, times(1)).getRates("USD");
    }

    private RatesProviderResponse buildSuccessfulResponse(
            String source,
            Map<String, BigDecimal> quotes) {

        RatesProviderResponse response = new RatesProviderResponse();
        response.setSuccess(true);
        response.setSource(source);
        response.setQuotes(quotes);
        return response;
    }
}