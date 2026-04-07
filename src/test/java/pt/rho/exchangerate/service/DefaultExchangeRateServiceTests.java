package pt.rho.exchangerate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import pt.rho.exchangerate.exception.ExchangeRateNotFoundException;
import pt.rho.exchangerate.exception.InvalidAmountException;
import pt.rho.exchangerate.exception.InvalidRequestException;
import pt.rho.exchangerate.model.ConversionResult;
import pt.rho.exchangerate.model.ExchangeRateResult;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.model.MultiConversionResult;

@ExtendWith(MockitoExtension.class)
class DefaultExchangeRateServiceTests {

    @Mock
    private CachedProviderService cachedProviderService;

    private DefaultExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
        exchangeRateService = new DefaultExchangeRateService(cachedProviderService);
    }

    @Test
    @DisplayName("getAllRates should delegate to lookup service")
    void shouldGetAllRatesForBaseCurrency() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of(
                        "EUR", new BigDecimal("0.92"),
                        "GBP", new BigDecimal("0.78"),
                        "JPY", new BigDecimal("150.10")
                )
        );

        when(cachedProviderService.getAllRates("USD")).thenReturn(exchangeRates);

        ExchangeRates result = exchangeRateService.getAllRates("USD");

        assertThat(result).isNotNull();
        assertThat(result.getBaseCurrency()).isEqualTo("USD");
        assertThat(result.getRates()).hasSize(3);
        assertThat(result.getRates()).containsEntry("EUR", new BigDecimal("0.92"));
        assertThat(result.getRates()).containsEntry("GBP", new BigDecimal("0.78"));
        assertThat(result.getRates()).containsEntry("JPY", new BigDecimal("150.10"));
        verify(cachedProviderService).getAllRates("USD");
    }

    @Test
    @DisplayName("getExchangeRate should return rate result")
    void shouldGetExchangeRateFromOneCurrencyToAnother() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of("EUR", new BigDecimal("0.92"))
        );

        when(cachedProviderService.getAllRates("USD")).thenReturn(exchangeRates);

        ExchangeRateResult result = exchangeRateService.getExchangeRate("USD", "EUR");

        assertThat(result).isNotNull();
        assertThat(result.getFromCurrency()).isEqualTo("USD");
        assertThat(result.getToCurrency()).isEqualTo("EUR");
        assertThat(result.getRate()).isEqualByComparingTo("0.92");
        verify(cachedProviderService).getAllRates("USD");
    }

    @Test
    @DisplayName("convert should convert amount from one currency to another")
    void shouldConvertAmountFromOneCurrencyToAnother() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of("EUR", new BigDecimal("0.92"))
        );

        when(cachedProviderService.getAllRates("USD")).thenReturn(exchangeRates);

        ConversionResult result = exchangeRateService.convert(
                "USD",
                "EUR",
                new BigDecimal("100")
        );

        assertThat(result).isNotNull();
        assertThat(result.getFromCurrency()).isEqualTo("USD");
        assertThat(result.getToCurrency()).isEqualTo("EUR");
        assertThat(result.getOriginalAmount()).isEqualByComparingTo("100");
        assertThat(result.getRate()).isEqualByComparingTo("0.92");
        assertThat(result.getConvertedAmount()).isEqualByComparingTo("92.000000");
        verify(cachedProviderService).getAllRates("USD");
    }

    @Test
    @DisplayName("convert should convert amount to multiple currencies")
    void shouldConvertAmountToMultipleCurrencies() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of(
                        "EUR", new BigDecimal("0.92"),
                        "GBP", new BigDecimal("0.78"),
                        "JPY", new BigDecimal("150.10")
                )
        );

        when(cachedProviderService.getAllRates("USD")).thenReturn(exchangeRates);

        MultiConversionResult result = exchangeRateService.convert(
                "USD",
                List.of("EUR", "GBP", "JPY"),
                new BigDecimal("100")
        );

        assertThat(result).isNotNull();
        assertThat(result.getFromCurrency()).isEqualTo("USD");
        assertThat(result.getOriginalAmount()).isEqualByComparingTo("100");
        assertThat(result.getConversions()).hasSize(3);

        assertThat(result.getConversions())
                .extracting(ConversionResult::getToCurrency)
                .containsExactly("EUR", "GBP", "JPY");

        assertThat(result.getConversions())
                .extracting(ConversionResult::getConvertedAmount)
                .containsExactly(
                        new BigDecimal("92.000000"),
                        new BigDecimal("78.000000"),
                        new BigDecimal("15010.000000")
                );

        verify(cachedProviderService).getAllRates("USD");
    }

    @Test
    @DisplayName("multi-convert should ignore duplicate target currencies")
    void shouldIgnoreDuplicateTargetCurrencies() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of(
                        "EUR", new BigDecimal("0.92"),
                        "GBP", new BigDecimal("0.78")
                )
        );

        when(cachedProviderService.getAllRates("USD")).thenReturn(exchangeRates);

        MultiConversionResult result = exchangeRateService.convert(
                "USD",
                List.of("EUR", "EUR", "GBP"),
                new BigDecimal("100")
        );

        assertThat(result.getConversions()).hasSize(2);
        assertThat(result.getConversions())
                .extracting(ConversionResult::getToCurrency)
                .containsExactly("EUR", "GBP");

        verify(cachedProviderService).getAllRates("USD");
    }

    @Test
    @DisplayName("convert should round converted amount to six decimal places")
    void shouldRoundConvertedAmountToSixDecimalPlaces() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of("EUR", new BigDecimal("0.9234567"))
        );

        when(cachedProviderService.getAllRates("USD")).thenReturn(exchangeRates);

        ConversionResult result = exchangeRateService.convert(
                "USD",
                "EUR",
                new BigDecimal("10")
        );

        assertThat(result.getConvertedAmount()).isEqualByComparingTo("9.234567");
        verify(cachedProviderService).getAllRates("USD");
    }

    @Test
    @DisplayName("getExchangeRate should throw when exchange rate is missing")
    void shouldThrowWhenExchangeRateIsMissing() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of("GBP", new BigDecimal("0.78"))
        );

        when(cachedProviderService.getAllRates("USD")).thenReturn(exchangeRates);

        assertThatThrownBy(() -> exchangeRateService.getExchangeRate("USD", "EUR"))
                .isInstanceOf(ExchangeRateNotFoundException.class)
                .hasMessage("Exchange rate not found from USD to EUR");

        verify(cachedProviderService).getAllRates("USD");
    }

    @Test
    @DisplayName("convert should throw when exchange rate is missing")
    void shouldThrowWhenConversionExchangeRateIsMissing() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of("GBP", new BigDecimal("0.78"))
        );

        when(cachedProviderService.getAllRates("USD")).thenReturn(exchangeRates);

        assertThatThrownBy(() -> exchangeRateService.convert("USD", "EUR", new BigDecimal("100")))
                .isInstanceOf(ExchangeRateNotFoundException.class)
                .hasMessage("Exchange rate not found from USD to EUR");

        verify(cachedProviderService).getAllRates("USD");
    }

    @Test
    @DisplayName("multi-convert should throw when one exchange rate is missing")
    void shouldThrowWhenMultiConversionExchangeRateIsMissing() {
        ExchangeRates exchangeRates = new ExchangeRates(
                "USD",
                Map.of("EUR", new BigDecimal("0.92"))
        );

        when(cachedProviderService.getAllRates("USD")).thenReturn(exchangeRates);

        assertThatThrownBy(() -> exchangeRateService.convert(
                "USD",
                List.of("EUR", "GBP"),
                new BigDecimal("100")
        ))
                .isInstanceOf(ExchangeRateNotFoundException.class)
                .hasMessage("Exchange rate not found from USD to GBP");

        verify(cachedProviderService).getAllRates("USD");
    }

    @Test
    @DisplayName("multi-convert should throw when target currencies list is empty")
    void shouldThrowWhenTargetCurrenciesListIsEmpty() {
        assertThatThrownBy(() -> exchangeRateService.convert(
                "USD",
                List.of(),
                new BigDecimal("100")
        ))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("Target currencies must not be empty");
    }

    @Test
    @DisplayName("convert should throw when amount is null")
    void shouldThrowWhenAmountIsNull() {
        assertThatThrownBy(() -> exchangeRateService.convert("USD", "EUR", null))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Amount must not be null");
    }

    @Test
    @DisplayName("convert should throw when amount is zero")
    void shouldThrowWhenAmountIsZero() {
        assertThatThrownBy(() -> exchangeRateService.convert(
                "USD",
                "EUR",
                BigDecimal.ZERO
        ))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Amount must be positive");
    }

    @Test
    @DisplayName("convert should throw when amount is negative")
    void shouldThrowWhenAmountIsNegative() {
        assertThatThrownBy(() -> exchangeRateService.convert(
                "USD",
                "EUR",
                new BigDecimal("-1")
        ))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Amount must be positive");
    }

    @Test
    @DisplayName("multi-convert should throw when amount is null")
    void shouldThrowWhenMultiConvertAmountIsNull() {
        assertThatThrownBy(() -> exchangeRateService.convert("USD", List.of("EUR"), null))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Amount must not be null");
    }

    @Test
    @DisplayName("multi-convert should throw when amount is zero")
    void shouldThrowWhenMultiConvertAmountIsZero() {
        assertThatThrownBy(() -> exchangeRateService.convert("USD", List.of("EUR"), BigDecimal.ZERO))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Amount must be positive");
    }

    @Test
    @DisplayName("multi-convert should throw when amount is negative")
    void shouldThrowWhenMultiConvertAmountIsNegative() {
        assertThatThrownBy(() -> exchangeRateService.convert("USD", List.of("EUR"), new BigDecimal("-1")))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Amount must be positive");
    }
}