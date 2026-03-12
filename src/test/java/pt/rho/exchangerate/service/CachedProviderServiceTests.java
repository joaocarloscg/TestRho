package pt.rho.exchangerate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pt.rho.exchangerate.mapper.RatesProviderResponseMapper;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.provider.RatesProvider;
import pt.rho.exchangerate.provider.dto.RatesProviderResponse;

@ExtendWith(MockitoExtension.class)
class CachedProviderServiceTests {

    @Mock
    private RatesProvider ratesProvider;

    @Mock
    private RatesProviderResponseMapper ratesProviderResponseMapper;

    @InjectMocks
    private CachedProviderService cachedProviderService;

    @Test
    @DisplayName("Should return mapped exchange rates from provider response")
    void shouldReturnMappedExchangeRatesFromProviderResponse() {
        RatesProviderResponse providerResponse = buildSuccessfulResponse(
                "USD",
                Map.of("USDEUR", new BigDecimal("0.92"))
        );

        ExchangeRates mappedResult = new ExchangeRates(
                "USD",
                Map.of("EUR", new BigDecimal("0.92"))
        );

        when(ratesProvider.getRates("USD")).thenReturn(providerResponse);
        when(ratesProviderResponseMapper.toExchangeRates("USD", providerResponse)).thenReturn(mappedResult);

        ExchangeRates result = cachedProviderService.getAllRates("USD");

        assertThat(result).isSameAs(mappedResult);
        verify(ratesProvider).getRates("USD");
        verify(ratesProviderResponseMapper).toExchangeRates("USD", providerResponse);
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