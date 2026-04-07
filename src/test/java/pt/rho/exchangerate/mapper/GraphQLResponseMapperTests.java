package pt.rho.exchangerate.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import pt.rho.exchangerate.dto.ConversionResponse;
import pt.rho.exchangerate.dto.ExchangeRateResponse;
import pt.rho.exchangerate.dto.ExchangeRatesGraphQlResponse;
import pt.rho.exchangerate.dto.MultiConversionResponse;
import pt.rho.exchangerate.model.ConversionResult;
import pt.rho.exchangerate.model.ExchangeRateResult;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.model.MultiConversionResult;

class GraphQLResponseMapperTests {

	private final GraphQLResponseMapper mapper = Mappers.getMapper(GraphQLResponseMapper.class);

    @Test
    @DisplayName("toExchangeRatesGraphQlResponse should sort currencies and apply optional filters")
    void shouldMapAndFilterExchangeRates() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of(
                        "GBP", new BigDecimal("0.78"),
                        "EUR", new BigDecimal("0.92"),
                        "JPY", new BigDecimal("150.10")
                )
        );

        ExchangeRatesGraphQlResponse response = mapper.toExchangeRatesGraphQlResponse(
                exchangeRates,
                List.of("jpy", "eur", "eur")
        );

        assertThat(response.getBase()).isEqualTo("USD");
        assertThat(response.getRates())
                .extracting(rate -> rate.getCurrency() + ":" + rate.getRate())
                .containsExactly("EUR:0.92", "JPY:150.10");
    }

    @Test
    @DisplayName("toExchangeRatesGraphQlResponse should return all currencies sorted when filter is null")
    void shouldMapAllExchangeRatesWhenFilterIsNull() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of(
                        "JPY", new BigDecimal("150.10"),
                        "EUR", new BigDecimal("0.92"),
                        "GBP", new BigDecimal("0.78")
                )
        );

        ExchangeRatesGraphQlResponse response = mapper.toExchangeRatesGraphQlResponse(exchangeRates, null);

        assertThat(response.getBase()).isEqualTo("USD");
        assertThat(response.getRates())
                .extracting(rate -> rate.getCurrency() + ":" + rate.getRate())
                .containsExactly("EUR:0.92", "GBP:0.78", "JPY:150.10");
    }

    @Test
    @DisplayName("toExchangeRatesGraphQlResponse should return all currencies sorted when filter is empty")
    void shouldMapAllExchangeRatesWhenFilterIsEmpty() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of(
                        "JPY", new BigDecimal("150.10"),
                        "EUR", new BigDecimal("0.92"),
                        "GBP", new BigDecimal("0.78")
                )
        );

        ExchangeRatesGraphQlResponse response = mapper.toExchangeRatesGraphQlResponse(exchangeRates, List.of());

        assertThat(response.getBase()).isEqualTo("USD");
        assertThat(response.getRates())
                .extracting(rate -> rate.getCurrency() + ":" + rate.getRate())
                .containsExactly("EUR:0.92", "GBP:0.78", "JPY:150.10");
    }

    @Test
    @DisplayName("toExchangeRatesGraphQlResponse should return empty rates when filter matches nothing")
    void shouldReturnEmptyRatesWhenFilterMatchesNothing() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of(
                        "EUR", new BigDecimal("0.92"),
                        "GBP", new BigDecimal("0.78")
                )
        );

        ExchangeRatesGraphQlResponse response = mapper.toExchangeRatesGraphQlResponse(
                exchangeRates,
                List.of("JPY")
        );

        assertThat(response.getBase()).isEqualTo("USD");
        assertThat(response.getRates()).isEmpty();
    }

    @Test
    @DisplayName("toExchangeRateResponse should map exchange rate result")
    void shouldMapExchangeRateResponse() {
        ExchangeRateResult result = new ExchangeRateResult(
                "USD",
                "EUR",
                new BigDecimal("0.92")
        );

        ExchangeRateResponse response = mapper.toExchangeRateResponse(result);

        assertThat(response.getFrom()).isEqualTo("USD");
        assertThat(response.getTo()).isEqualTo("EUR");
        assertThat(response.getRate()).isEqualByComparingTo("0.92");
    }

    @Test
    @DisplayName("toConversionResponse should map conversion result")
    void shouldMapConversionResponse() {
        ConversionResult result = new ConversionResult(
                "USD",
                "EUR",
                new BigDecimal("100"),
                new BigDecimal("0.92"),
                new BigDecimal("92.000000")
        );

        ConversionResponse response = mapper.toConversionResponse(result);

        assertThat(response.getFrom()).isEqualTo("USD");
        assertThat(response.getTo()).isEqualTo("EUR");
        assertThat(response.getOriginalAmount()).isEqualByComparingTo("100");
        assertThat(response.getRate()).isEqualByComparingTo("0.92");
        assertThat(response.getConvertedAmount()).isEqualByComparingTo("92.000000");
    }

    @Test
    @DisplayName("toMultiConversionResponse should map multi conversion result")
    void shouldMapMultiConversionResponse() {
        MultiConversionResult result = new MultiConversionResult(
                "USD",
                new BigDecimal("100"),
                List.of(
                        new ConversionResult(
                                "USD",
                                "EUR",
                                new BigDecimal("100"),
                                new BigDecimal("0.92"),
                                new BigDecimal("92.000000")
                        ),
                        new ConversionResult(
                                "USD",
                                "GBP",
                                new BigDecimal("100"),
                                new BigDecimal("0.78"),
                                new BigDecimal("78.000000")
                        )
                )
        );

        MultiConversionResponse response = mapper.toMultiConversionResponse(result);

        assertThat(response.getFrom()).isEqualTo("USD");
        assertThat(response.getOriginalAmount()).isEqualByComparingTo("100");
        assertThat(response.getConversions()).hasSize(2);

        assertThat(response.getConversions().get(0).getCurrency()).isEqualTo("EUR");
        assertThat(response.getConversions().get(0).getRate()).isEqualByComparingTo("0.92");
        assertThat(response.getConversions().get(0).getConvertedAmount()).isEqualByComparingTo("92.000000");

        assertThat(response.getConversions().get(1).getCurrency()).isEqualTo("GBP");
        assertThat(response.getConversions().get(1).getRate()).isEqualByComparingTo("0.78");
        assertThat(response.getConversions().get(1).getConvertedAmount()).isEqualByComparingTo("78.000000");
    }
}