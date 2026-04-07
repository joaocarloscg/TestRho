package pt.rho.exchangerate.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.util.UriBuilder;

import pt.rho.exchangerate.config.ApplicationProperties;
import pt.rho.exchangerate.exception.ExternalProviderException;
import pt.rho.exchangerate.provider.dto.RatesProviderResponse;

@ExtendWith(MockitoExtension.class)
class DefaultRatesProviderTests {

    @Mock
    private RestClient restClient;

    @Mock
    private RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private ApplicationProperties applicationProperties;

    private DefaultRatesProvider defaultRatesProvider;

    @BeforeEach
    void setUp() {
        defaultRatesProvider = new DefaultRatesProvider(
                restClient,
                applicationProperties
        );
    }

    @Test
    @DisplayName("Should return rates provider response when external call succeeds")
    void shouldReturnRatesProviderResponseWhenExternalCallSucceeds() {
        RatesProviderResponse response = new RatesProviderResponse();

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec)
                .uri(org.mockito.ArgumentMatchers.<Function<UriBuilder, URI>>any());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(RatesProviderResponse.class)).thenReturn(response);

        RatesProviderResponse result = defaultRatesProvider.getRates(" usd ");

        assertThat(result).isSameAs(response);
        verify(restClient).get();
        verify(responseSpec).body(RatesProviderResponse.class);
    }

    @Test
    @DisplayName("Should use normalized base currency when calling external provider")
    void shouldUseNormalizedBaseCurrencyWhenCallingExternalProvider() {
        RatesProviderResponse response = new RatesProviderResponse();

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec)
                .uri(org.mockito.ArgumentMatchers.<Function<UriBuilder, URI>>any());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(RatesProviderResponse.class)).thenReturn(response);

        RatesProviderResponse result = defaultRatesProvider.getRates("eur");

        assertThat(result).isSameAs(response);
        verify(restClient).get();
    }

    @Test
    @DisplayName("Should throw ExternalProviderException when external provider returns null response")
    void shouldThrowWhenExternalProviderReturnsNullResponse() {
        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec)
                .uri(org.mockito.ArgumentMatchers.<Function<UriBuilder, URI>>any());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(RatesProviderResponse.class)).thenReturn(null);

        assertThatThrownBy(() -> defaultRatesProvider.getRates("USD"))
                .isInstanceOf(ExternalProviderException.class)
                .hasMessage("Exchange rate provider returned an empty response");
    }
}