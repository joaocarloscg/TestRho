package pt.rho.exchangerate.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import pt.rho.exchangerate.dto.ConversionResponse;
import pt.rho.exchangerate.dto.ExchangeRateResponse;
import pt.rho.exchangerate.dto.ExchangeRatesResponse;
import pt.rho.exchangerate.dto.MultiConversionResponse;
import pt.rho.exchangerate.dto.SingleConversionItemResponse;
import pt.rho.exchangerate.model.ConversionResult;
import pt.rho.exchangerate.model.ExchangeRateResult;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.model.MultiConversionResult;

class ApiResponseMapperTests {

    private final ApiResponseMapper mapper = new ApiResponseMapper();

    @Test
    @DisplayName("Should map ExchangeRates to ExchangeRatesResponse")
    void shouldMapExchangeRatesToExchangeRatesResponse() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of(
                        "EUR", new BigDecimal("0.92"),
                        "GBP", new BigDecimal("0.78")
                )
        );

        ExchangeRatesResponse result = mapper.toExchangeRatesResponse(exchangeRates);

        assertThat(result).isNotNull();
        assertThat(result.getBase()).isEqualTo("USD");
        assertThat(result.getRates()).containsEntry("EUR", new BigDecimal("0.92"));
        assertThat(result.getRates()).containsEntry("GBP", new BigDecimal("0.78"));
    }

    @Test
    @DisplayName("Should map ExchangeRateResult to ExchangeRateResponse")
    void shouldMapExchangeRateResultToExchangeRateResponse() {
        ExchangeRateResult exchangeRateResult = new ExchangeRateResult(
                "USD",
                "EUR",
                new BigDecimal("0.92")
        );

        ExchangeRateResponse result = mapper.toExchangeRateResponse(exchangeRateResult);

        assertThat(result).isNotNull();
        assertThat(result.getFrom()).isEqualTo("USD");
        assertThat(result.getTo()).isEqualTo("EUR");
        assertThat(result.getRate()).isEqualByComparingTo("0.92");
    }

    @Test
    @DisplayName("Should map ConversionResult to ConversionResponse")
    void shouldMapConversionResultToConversionResponse() {
        ConversionResult conversionResult = new ConversionResult(
                "USD",
                "EUR",
                new BigDecimal("100"),
                new BigDecimal("0.92"),
                new BigDecimal("92.000000")
        );

        ConversionResponse result = mapper.toConversionResponse(conversionResult);

        assertThat(result).isNotNull();
        assertThat(result.getFrom()).isEqualTo("USD");
        assertThat(result.getTo()).isEqualTo("EUR");
        assertThat(result.getOriginalAmount()).isEqualByComparingTo("100");
        assertThat(result.getRate()).isEqualByComparingTo("0.92");
        assertThat(result.getConvertedAmount()).isEqualByComparingTo("92.000000");
    }

    @Test
    @DisplayName("Should map ConversionResult to SingleConversionItemResponse")
    void shouldMapConversionResultToSingleConversionItemResponse() {
        ConversionResult conversionResult = new ConversionResult(
                "USD",
                "EUR",
                new BigDecimal("100"),
                new BigDecimal("0.92"),
                new BigDecimal("92.000000")
        );

        SingleConversionItemResponse result = mapper.toSingleConversionItemResponse(conversionResult);

        assertThat(result).isNotNull();
        assertThat(result.getCurrency()).isEqualTo("EUR");
        assertThat(result.getRate()).isEqualByComparingTo("0.92");
        assertThat(result.getConvertedAmount()).isEqualByComparingTo("92.000000");
    }

    @Test
    @DisplayName("Should map MultiConversionResult to MultiConversionResponse")
    void shouldMapMultiConversionResultToMultiConversionResponse() {
        MultiConversionResult multiConversionResult = new MultiConversionResult(
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

        MultiConversionResponse result = mapper.toMultiConversionResponse(multiConversionResult);

        assertThat(result).isNotNull();
        assertThat(result.getFrom()).isEqualTo("USD");
        assertThat(result.getOriginalAmount()).isEqualByComparingTo("100");
        assertThat(result.getConversions()).hasSize(2);

        assertThat(result.getConversions().get(0).getCurrency()).isEqualTo("EUR");
        assertThat(result.getConversions().get(0).getRate()).isEqualByComparingTo("0.92");
        assertThat(result.getConversions().get(0).getConvertedAmount()).isEqualByComparingTo("92.000000");

        assertThat(result.getConversions().get(1).getCurrency()).isEqualTo("GBP");
        assertThat(result.getConversions().get(1).getRate()).isEqualByComparingTo("0.78");
        assertThat(result.getConversions().get(1).getConvertedAmount()).isEqualByComparingTo("78.000000");
    }
}