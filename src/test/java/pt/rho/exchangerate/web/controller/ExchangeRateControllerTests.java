package pt.rho.exchangerate.web.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import pt.rho.exchangerate.dto.ConversionResponse;
import pt.rho.exchangerate.dto.ExchangeRateResponse;
import pt.rho.exchangerate.dto.ExchangeRatesResponse;
import pt.rho.exchangerate.dto.MultiConversionResponse;
import pt.rho.exchangerate.dto.SingleConversionItemResponse;
import pt.rho.exchangerate.exception.ExchangeRateNotFoundException;
import pt.rho.exchangerate.mapper.ApiResponseMapper;
import pt.rho.exchangerate.model.ConversionResult;
import pt.rho.exchangerate.model.ExchangeRateResult;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.model.MultiConversionResult;
import pt.rho.exchangerate.service.ExchangeRateService;
import pt.rho.exchangerate.web.ErrorResponseFactory;
import pt.rho.exchangerate.web.GlobalExceptionHandler;
import pt.rho.exchangerate.web.RateLimitingFilter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ExchangeRateController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = RateLimitingFilter.class
        )
)
@Import({GlobalExceptionHandler.class, ErrorResponseFactory.class})
class ExchangeRateControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExchangeRateService exchangeRateService;

    @MockitoBean
    private ApiResponseMapper apiResponseMapper;

    @Test
    @DisplayName("GET /api/v1/rates?base=USD should return all rates for base currency")
    void shouldReturnAllRates() throws Exception {
        ExchangeRates serviceResult = new ExchangeRates(
                "USD",
                Map.of(
                        "EUR", new BigDecimal("0.92"),
                        "GBP", new BigDecimal("0.78")
                )
        );

        ExchangeRatesResponse response = new ExchangeRatesResponse(
                "USD",
                Map.of(
                        "EUR", new BigDecimal("0.92"),
                        "GBP", new BigDecimal("0.78")
                )
        );

        when(exchangeRateService.getAllRates("USD")).thenReturn(serviceResult);
        when(apiResponseMapper.toExchangeRatesResponse(serviceResult)).thenReturn(response);

        mockMvc.perform(get("/api/v1/rates").param("base", "USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base").value("USD"))
                .andExpect(jsonPath("$.rates.EUR").value(0.92))
                .andExpect(jsonPath("$.rates.GBP").value(0.78));

        verify(exchangeRateService).getAllRates("USD");
        verify(apiResponseMapper).toExchangeRatesResponse(serviceResult);
    }

    @Test
    @DisplayName("GET /api/v1/rates/USD/EUR should return exchange rate")
    void shouldReturnExchangeRate() throws Exception {
        ExchangeRateResult serviceResult =
                new ExchangeRateResult("USD", "EUR", new BigDecimal("0.92"));

        ExchangeRateResponse response =
                new ExchangeRateResponse("USD", "EUR", new BigDecimal("0.92"));

        when(exchangeRateService.getExchangeRate("USD", "EUR")).thenReturn(serviceResult);
        when(apiResponseMapper.toExchangeRateResponse(serviceResult)).thenReturn(response);

        mockMvc.perform(get("/api/v1/rates/USD/EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("USD"))
                .andExpect(jsonPath("$.to").value("EUR"))
                .andExpect(jsonPath("$.rate").value(0.92));

        verify(exchangeRateService).getExchangeRate("USD", "EUR");
        verify(apiResponseMapper).toExchangeRateResponse(serviceResult);
    }

    @Test
    @DisplayName("GET /api/v1/rates/USD/EUR should return 404 when rate not found")
    void shouldReturnNotFoundWhenRateMissing() throws Exception {
        when(exchangeRateService.getExchangeRate("USD", "EUR"))
                .thenThrow(new ExchangeRateNotFoundException("Exchange rate not found from USD to EUR"));

        mockMvc.perform(get("/api/v1/rates/USD/EUR"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Exchange rate not found from USD to EUR"));
    }

    @Test
    @DisplayName("GET /api/v1/rates/US/EUR should return 400 for invalid source currency")
    void shouldReturnBadRequestForInvalidSourceCurrency() throws Exception {
        mockMvc.perform(get("/api/v1/rates/US/EUR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Currency must be a 3-letter ISO-style code"));

        verifyNoInteractions(exchangeRateService);
    }

    @Test
    @DisplayName("GET /api/v1/rates/USD/EURO should return 400 for invalid target currency")
    void shouldReturnBadRequestForInvalidTargetCurrency() throws Exception {
        mockMvc.perform(get("/api/v1/rates/USD/EURO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Currency must be a 3-letter ISO-style code"));

        verifyNoInteractions(exchangeRateService);
    }

    @Test
    @DisplayName("GET /api/v1/rates?base=EU should return 400 for invalid base currency")
    void shouldReturnBadRequestForInvalidBaseCurrency() throws Exception {
        mockMvc.perform(get("/api/v1/rates").param("base", "EU"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Currency must be a 3-letter ISO-style code"));

        verifyNoInteractions(exchangeRateService);
    }

    @Test
    @DisplayName("GET /api/v1/conversions/USD/EUR?amount=100 should convert amount")
    void shouldConvertAmount() throws Exception {
        ConversionResult serviceResult = new ConversionResult(
                "USD",
                "EUR",
                new BigDecimal("100"),
                new BigDecimal("0.92"),
                new BigDecimal("92.000000")
        );

        ConversionResponse response = new ConversionResponse(
                "USD",
                "EUR",
                new BigDecimal("100"),
                new BigDecimal("0.92"),
                new BigDecimal("92.000000")
        );

        when(exchangeRateService.convert("USD", "EUR", new BigDecimal("100")))
                .thenReturn(serviceResult);
        when(apiResponseMapper.toConversionResponse(serviceResult))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/conversions/USD/EUR")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("USD"))
                .andExpect(jsonPath("$.to").value("EUR"))
                .andExpect(jsonPath("$.originalAmount").value(100))
                .andExpect(jsonPath("$.rate").value(0.92))
                .andExpect(jsonPath("$.convertedAmount").value(92.000000));

        verify(exchangeRateService).convert("USD", "EUR", new BigDecimal("100"));
        verify(apiResponseMapper).toConversionResponse(serviceResult);
    }

    @Test
    @DisplayName("GET /api/v1/conversions/US/EUR should return 400 for invalid source currency")
    void shouldReturnBadRequestForInvalidConversionSourceCurrency() throws Exception {
        mockMvc.perform(get("/api/v1/conversions/US/EUR")
                        .param("amount", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Currency must be a 3-letter ISO-style code"));

        verifyNoInteractions(exchangeRateService);
    }

    @Test
    @DisplayName("GET /api/v1/conversions/USD/EU should return 400 for invalid target currency")
    void shouldReturnBadRequestForInvalidConversionTargetCurrency() throws Exception {
        mockMvc.perform(get("/api/v1/conversions/USD/EU")
                        .param("amount", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Currency must be a 3-letter ISO-style code"));

        verifyNoInteractions(exchangeRateService);
    }

    @Test
    @DisplayName("GET /api/v1/conversions/USD?amount=100&to=EUR&to=GBP should convert to multiple currencies")
    void shouldConvertMultipleCurrencies() throws Exception {
        MultiConversionResult serviceResult = new MultiConversionResult(
                "USD",
                new BigDecimal("100"),
                List.of(
                        new ConversionResult("USD", "EUR", new BigDecimal("100"), new BigDecimal("0.92"), new BigDecimal("92.000000")),
                        new ConversionResult("USD", "GBP", new BigDecimal("100"), new BigDecimal("0.78"), new BigDecimal("78.000000"))
                )
        );

        MultiConversionResponse response = new MultiConversionResponse(
                "USD",
                new BigDecimal("100"),
                List.of(
                        new SingleConversionItemResponse("EUR", new BigDecimal("0.92"), new BigDecimal("92.000000")),
                        new SingleConversionItemResponse("GBP", new BigDecimal("0.78"), new BigDecimal("78.000000"))
                )
        );

        when(exchangeRateService.convert(
                eq("USD"),
                eq(List.of("EUR", "GBP")),
                eq(new BigDecimal("100"))
        )).thenReturn(serviceResult);

        when(apiResponseMapper.toMultiConversionResponse(serviceResult)).thenReturn(response);

        mockMvc.perform(get("/api/v1/conversions/USD")
                        .param("amount", "100")
                        .param("to", "EUR", "GBP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("USD"))
                .andExpect(jsonPath("$.originalAmount").value(100))
                .andExpect(jsonPath("$.conversions.length()").value(2))
                .andExpect(jsonPath("$.conversions[0].currency").value("EUR"))
                .andExpect(jsonPath("$.conversions[0].rate").value(0.92))
                .andExpect(jsonPath("$.conversions[0].convertedAmount").value(92.000000))
                .andExpect(jsonPath("$.conversions[1].currency").value("GBP"))
                .andExpect(jsonPath("$.conversions[1].rate").value(0.78))
                .andExpect(jsonPath("$.conversions[1].convertedAmount").value(78.000000));

        verify(exchangeRateService).convert("USD", List.of("EUR", "GBP"), new BigDecimal("100"));
        verify(apiResponseMapper).toMultiConversionResponse(serviceResult);
    }

    @Test
    @DisplayName("GET /api/v1/conversions/USD?amount=100&to=EU should return 400 for invalid target currency list item")
    void shouldReturnBadRequestForInvalidMultiConversionTargetCurrency() throws Exception {
        mockMvc.perform(get("/api/v1/conversions/USD")
                        .param("amount", "100")
                        .param("to", "EU"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Currency must be a 3-letter ISO-style code"));

        verifyNoInteractions(exchangeRateService);
    }

    @Test
    @DisplayName("GET /api/v1/conversions/USD should return 400 when target currencies are missing")
    void shouldReturnBadRequestWhenTargetCurrenciesAreMissing() throws Exception {
        mockMvc.perform(get("/api/v1/conversions/USD")
                        .param("amount", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Required request parameter 'to' is missing"));

        verifyNoInteractions(exchangeRateService);
    }

    @Test
    @DisplayName("GET /api/v1/rates?base=USD should return 502 when external provider fails")
    void shouldReturnBadGatewayWhenExternalProviderFails() throws Exception {
        when(exchangeRateService.getAllRates("USD"))
                .thenThrow(new RestClientException("Provider unavailable"));

        mockMvc.perform(get("/api/v1/rates").param("base", "USD"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.error").value("Bad Gateway"))
                .andExpect(jsonPath("$.message").value("Failed to retrieve data from external exchange rate provider"))
                .andExpect(jsonPath("$.path").value("/api/v1/rates"));
    }

    @Test
    @DisplayName("GET /api/v1/rates/USD/EUR should return 500 for unexpected exception")
    void shouldReturnInternalServerErrorForUnexpectedException() throws Exception {
        when(exchangeRateService.getExchangeRate("USD", "EUR"))
                .thenThrow(new RuntimeException("Unexpected"));

        mockMvc.perform(get("/api/v1/rates/USD/EUR"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }
}