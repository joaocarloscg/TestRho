package pt.rho.exchangerate.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import pt.rho.exchangerate.exception.ExternalProviderException;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.provider.dto.RatesProviderResponse;
import pt.rho.exchangerate.provider.dto.RatesProviderResponseError;

class RatesProviderResponseMapperTests {

    private RatesProviderResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RatesProviderResponseMapper();
    }

    @Test
    @DisplayName("Should map valid provider response to ExchangeRates")
    void shouldMapValidResponse() {
        RatesProviderResponse response = buildSuccessfulResponse(
                "USD",
                Map.of(
                        "USDEUR", new BigDecimal("0.92"),
                        "USDGBP", new BigDecimal("0.78")
                )
        );

        ExchangeRates result = mapper.toExchangeRates("USD", response);

        assertThat(result).isNotNull();
        assertThat(result.getBaseCurrency()).isEqualTo("USD");
        assertThat(result.getRates()).containsEntry("EUR", new BigDecimal("0.92"));
        assertThat(result.getRates()).containsEntry("GBP", new BigDecimal("0.78"));
    }

    @Test
    @DisplayName("Should throw ExternalProviderException when response is null")
    void shouldThrowWhenResponseIsNull() {
        assertThatThrownBy(() -> mapper.toExchangeRates("USD", null))
                .isInstanceOf(ExternalProviderException.class)
                .hasMessage("No response received from exchange rate provider");
    }

    @Test
    @DisplayName("Should throw ExternalProviderException when provider response is unsuccessful with error info")
    void shouldThrowWhenResponseIsUnsuccessfulWithErrorInfo() {
        RatesProviderResponse response = new RatesProviderResponse();
        response.setSuccess(false);

        RatesProviderResponseError error = new RatesProviderResponseError();
        error.setCode(101);
        error.setInfo("Invalid API key");
        response.setError(error);

        assertThatThrownBy(() -> mapper.toExchangeRates("USD", response))
                .isInstanceOf(ExternalProviderException.class)
                .hasMessage("Failed to fetch exchange rates from external provider");
    }

    @Test
    @DisplayName("Should throw ExternalProviderException when provider response is unsuccessful without error info")
    void shouldThrowGenericMessageWhenResponseIsUnsuccessfulWithoutErrorInfo() {
        RatesProviderResponse response = new RatesProviderResponse();
        response.setSuccess(false);

        assertThatThrownBy(() -> mapper.toExchangeRates("USD", response))
                .isInstanceOf(ExternalProviderException.class)
                .hasMessage("Failed to fetch exchange rates from external provider");
    }

    @Test
    @DisplayName("Should throw ExternalProviderException when provider returns no quotes")
    void shouldThrowWhenQuotesAreEmpty() {
        RatesProviderResponse response = buildSuccessfulResponse("USD", Map.of());

        assertThatThrownBy(() -> mapper.toExchangeRates("USD", response))
                .isInstanceOf(ExternalProviderException.class)
                .hasMessage("No exchange rates returned by external provider for base currency USD");
    }

    @Test
    @DisplayName("Should throw ExternalProviderException when provider returns null quotes")
    void shouldThrowWhenQuotesAreNull() {
        RatesProviderResponse response = new RatesProviderResponse();
        response.setSuccess(true);
        response.setSource("USD");
        response.setQuotes(null);

        assertThatThrownBy(() -> mapper.toExchangeRates("USD", response))
                .isInstanceOf(ExternalProviderException.class)
                .hasMessage("No exchange rates returned by external provider for base currency USD");
    }

    @Test
    @DisplayName("Should throw ExternalProviderException when no quotes match requested base currency")
    void shouldThrowWhenNoQuotesMatchBaseCurrency() {
        RatesProviderResponse response = buildSuccessfulResponse(
                "USD",
                Map.of("EUREUR", new BigDecimal("1.00"))
        );

        assertThatThrownBy(() -> mapper.toExchangeRates("USD", response))
                .isInstanceOf(ExternalProviderException.class)
                .hasMessage("No valid exchange rates found for base currency USD");
    }

    @Test
    @DisplayName("Should ignore malformed quote keys that do not match expected format")
    void shouldIgnoreMalformedQuoteKeys() {
        RatesProviderResponse response = buildSuccessfulResponse(
                "USD",
                Map.of(
                        "USD", new BigDecimal("1.00"),
                        "USDX", new BigDecimal("2.00"),
                        "USDEUR", new BigDecimal("0.92")
                )
        );

        ExchangeRates result = mapper.toExchangeRates("USD", response);

        assertThat(result.getRates()).hasSize(1);
        assertThat(result.getRates()).containsEntry("EUR", new BigDecimal("0.92"));
    }

    @Test
    @DisplayName("Should ignore quote keys with null key")
    void shouldIgnoreNullQuoteKeys() {
        RatesProviderResponse response = new RatesProviderResponse();
        response.setSuccess(true);
        response.setSource("USD");
        response.setQuotes(Map.of("USDEUR", new BigDecimal("0.92")));

        ExchangeRates result = mapper.toExchangeRates("USD", response);

        assertThat(result.getRates()).hasSize(1);
        assertThat(result.getRates()).containsEntry("EUR", new BigDecimal("0.92"));
    }

    @Test
    @DisplayName("Should map only quotes that start with requested base currency")
    void shouldMapOnlyQuotesForRequestedBaseCurrency() {
        RatesProviderResponse response = buildSuccessfulResponse(
                "USD",
                Map.of(
                        "USDEUR", new BigDecimal("0.92"),
                        "USDGBP", new BigDecimal("0.78"),
                        "EUREUR", new BigDecimal("1.00")
                )
        );

        ExchangeRates result = mapper.toExchangeRates("USD", response);

        assertThat(result.getRates()).hasSize(2);
        assertThat(result.getRates()).containsEntry("EUR", new BigDecimal("0.92"));
        assertThat(result.getRates()).containsEntry("GBP", new BigDecimal("0.78"));
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