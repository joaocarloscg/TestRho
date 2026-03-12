package pt.rho.exchangerate.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pt.rho.exchangerate.dto.ConversionResponse;
import pt.rho.exchangerate.dto.ExchangeRateResponse;
import pt.rho.exchangerate.dto.MultiConversionResponse;
import pt.rho.exchangerate.dto.SingleConversionItemResponse;
import pt.rho.exchangerate.graphql.dto.CurrencyRateGraphQlResponse;
import pt.rho.exchangerate.graphql.dto.ExchangeRatesGraphQlResponse;
import pt.rho.exchangerate.model.ConversionResult;
import pt.rho.exchangerate.model.ExchangeRateResult;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.model.MultiConversionResult;
import pt.rho.exchangerate.service.ExchangeRateService;

@ExtendWith(MockitoExtension.class)
class ExchangeRateGraphQlControllerTests {

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private ResponseMapper responseMapper;

    private ExchangeRateGraphQlController controller;

    @BeforeEach
    void setUp() {
        controller = new ExchangeRateGraphQlController(exchangeRateService, responseMapper);
    }

    @Test
    @DisplayName("exchangeRates should return mapped GraphQL exchange rates response")
    void shouldReturnMappedExchangeRatesResponse() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of(
                        "EUR", new BigDecimal("0.92"),
                        "GBP", new BigDecimal("0.78")
                )
        );

        ExchangeRatesGraphQlResponse mappedResponse = new ExchangeRatesGraphQlResponse(
                "USD",
                List.of(
                        new CurrencyRateGraphQlResponse("EUR", new BigDecimal("0.92")),
                        new CurrencyRateGraphQlResponse("GBP", new BigDecimal("0.78"))
                )
        );

        when(exchangeRateService.getAllRates("USD")).thenReturn(exchangeRates);
        when(responseMapper.toExchangeRatesGraphQlResponse(exchangeRates, List.of("EUR", "GBP")))
                .thenReturn(mappedResponse);

        ExchangeRatesGraphQlResponse result = controller.exchangeRates("USD", List.of("EUR", "GBP"));

        assertThat(result).isSameAs(mappedResponse);
        verify(exchangeRateService).getAllRates("USD");
        verify(responseMapper).toExchangeRatesGraphQlResponse(exchangeRates, List.of("EUR", "GBP"));
    }

    @Test
    @DisplayName("exchangeRate should return mapped exchange rate response")
    void shouldReturnMappedExchangeRateResponse() {
        ExchangeRateResult exchangeRateResult = new ExchangeRateResult(
                "USD",
                "EUR",
                new BigDecimal("0.92")
        );

        ExchangeRateResponse mappedResponse = new ExchangeRateResponse(
                "USD",
                "EUR",
                new BigDecimal("0.92")
        );

        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(exchangeRateResult);
        when(responseMapper.toExchangeRateResponse(exchangeRateResult)).thenReturn(mappedResponse);

        ExchangeRateResponse result = controller.exchangeRate("USD", "EUR");

        assertThat(result).isSameAs(mappedResponse);
        verify(exchangeRateService).getExchangeRate("USD", "EUR");
        verify(responseMapper).toExchangeRateResponse(exchangeRateResult);
    }

    @Test
    @DisplayName("convert should return mapped conversion response")
    void shouldReturnMappedConversionResponse() {
        ConversionResult conversionResult = new ConversionResult(
                "USD",
                "EUR",
                new BigDecimal("100"),
                new BigDecimal("0.92"),
                new BigDecimal("92.000000")
        );

        ConversionResponse mappedResponse = new ConversionResponse(
                "USD",
                "EUR",
                new BigDecimal("100"),
                new BigDecimal("0.92"),
                new BigDecimal("92.000000")
        );

        when(exchangeRateService.convert("USD", "EUR", new BigDecimal("100"))).thenReturn(conversionResult);
        when(responseMapper.toConversionResponse(conversionResult)).thenReturn(mappedResponse);

        ConversionResponse result = controller.convert("USD", "EUR", new BigDecimal("100"));

        assertThat(result).isSameAs(mappedResponse);
        verify(exchangeRateService).convert("USD", "EUR", new BigDecimal("100"));
        verify(responseMapper).toConversionResponse(conversionResult);
    }

    @Test
    @DisplayName("convertMultiple should return mapped multi conversion response")
    void shouldReturnMappedMultiConversionResponse() {
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

        MultiConversionResponse mappedResponse = new MultiConversionResponse(
                "USD",
                new BigDecimal("100"),
                List.of(
                        new SingleConversionItemResponse(
                                "EUR",
                                new BigDecimal("0.92"),
                                new BigDecimal("92.000000")
                        ),
                        new SingleConversionItemResponse(
                                "GBP",
                                new BigDecimal("0.78"),
                                new BigDecimal("78.000000")
                        )
                )
        );

        when(exchangeRateService.convert(
                "USD",
                List.of("EUR", "GBP"),
                new BigDecimal("100")
        )).thenReturn(multiConversionResult);
        when(responseMapper.toMultiConversionResponse(multiConversionResult)).thenReturn(mappedResponse);

        MultiConversionResponse result = controller.convertMultiple(
                "USD",
                List.of("EUR", "GBP"),
                new BigDecimal("100")
        );

        assertThat(result).isSameAs(mappedResponse);
        verify(exchangeRateService).convert("USD", List.of("EUR", "GBP"), new BigDecimal("100"));
        verify(responseMapper).toMultiConversionResponse(multiConversionResult);
    }
}