package pt.rho.exchangerate.web.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyCodeValidatorTest {

    private final CurrencyCodeValidator validator = new CurrencyCodeValidator();

    @Test
    @DisplayName("Should accept uppercase 3-letter currency")
    void shouldAcceptUppercaseCurrency() {
        boolean result = validator.isValid("USD", null);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should accept lowercase 3-letter currency")
    void shouldAcceptLowercaseCurrency() {
        boolean result = validator.isValid("eur", null);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should accept mixed case 3-letter currency")
    void shouldAcceptMixedCaseCurrency() {
        boolean result = validator.isValid("gBp", null);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should accept currency with leading and trailing spaces")
    void shouldAcceptCurrencyWithSpaces() {
        boolean result = validator.isValid(" usd ", null);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should reject null currency")
    void shouldRejectNullCurrency() {
        boolean result = validator.isValid(null, null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should reject blank currency")
    void shouldRejectBlankCurrency() {
        boolean result = validator.isValid("   ", null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should reject currency shorter than 3 characters")
    void shouldRejectCurrencyShorterThanThreeCharacters() {
        boolean result = validator.isValid("US", null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should reject currency longer than 3 characters")
    void shouldRejectCurrencyLongerThanThreeCharacters() {
        boolean result = validator.isValid("USDT", null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should reject currency containing digits")
    void shouldRejectCurrencyContainingDigits() {
        boolean result = validator.isValid("U1D", null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should reject currency containing special characters")
    void shouldRejectCurrencyContainingSpecialCharacters() {
        boolean result = validator.isValid("U-D", null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should reject currency containing internal whitespace")
    void shouldRejectCurrencyContainingInternalWhitespace() {
        boolean result = validator.isValid("U SD", null);

        assertThat(result).isFalse();
    }
}