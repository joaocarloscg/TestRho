package pt.rho.exchangerate.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.rho.exchangerate.exception.InvalidCurrencyException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrencyValidatorTest {

    private final CurrencyValidator currencyValidator = new CurrencyValidator();

    @Test
    @DisplayName("Should normalize valid currency with spaces and lowercase")
    void shouldNormalizeValidCurrency() {
        String result = currencyValidator.validateAndNormalize(" usd ");

        assertThat(result).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should normalize lowercase currency")
    void shouldNormalizeLowercaseCurrency() {
        String result = currencyValidator.validateAndNormalize("eur");

        assertThat(result).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should normalize mixed case currency")
    void shouldNormalizeMixedCaseCurrency() {
        String result = currencyValidator.validateAndNormalize("gBp");

        assertThat(result).isEqualTo("GBP");
    }

    @Test
    @DisplayName("Should normalize currency with leading and trailing spaces")
    void shouldNormalizeCurrencyWithSpaces() {
        String result = currencyValidator.validateAndNormalize("  jpy  ");

        assertThat(result).isEqualTo("JPY");
    }

    @Test
    @DisplayName("Should throw InvalidCurrencyException when currency is null")
    void shouldThrowWhenCurrencyIsNull() {
        assertThatThrownBy(() -> currencyValidator.validateAndNormalize(null))
                .isInstanceOf(InvalidCurrencyException.class)
                .hasMessage("Currency must not be blank");
    }

    @Test
    @DisplayName("Should throw InvalidCurrencyException when currency is blank")
    void shouldThrowWhenCurrencyIsBlank() {
        assertThatThrownBy(() -> currencyValidator.validateAndNormalize("   "))
                .isInstanceOf(InvalidCurrencyException.class)
                .hasMessage("Currency must not be blank");
    }

    @Test
    @DisplayName("Should throw InvalidCurrencyException when currency length is less than three")
    void shouldThrowWhenCurrencyLengthIsLessThanThree() {
        assertThatThrownBy(() -> currencyValidator.validateAndNormalize("EU"))
                .isInstanceOf(InvalidCurrencyException.class)
                .hasMessage("Currency must have exactly 3 characters");
    }

    @Test
    @DisplayName("Should throw InvalidCurrencyException when currency length is greater than three")
    void shouldThrowWhenCurrencyLengthIsGreaterThanThree() {
        assertThatThrownBy(() -> currencyValidator.validateAndNormalize("EURO"))
                .isInstanceOf(InvalidCurrencyException.class)
                .hasMessage("Currency must have exactly 3 characters");
    }

    @Test
    @DisplayName("Should throw InvalidCurrencyException when currency contains digits")
    void shouldThrowWhenCurrencyContainsDigits() {
        assertThatThrownBy(() -> currencyValidator.validateAndNormalize("E1R"))
                .isInstanceOf(InvalidCurrencyException.class)
                .hasMessage("Currency must contain only letters");
    }

    @Test
    @DisplayName("Should throw InvalidCurrencyException when currency contains special characters")
    void shouldThrowWhenCurrencyContainsSpecialCharacters() {
        assertThatThrownBy(() -> currencyValidator.validateAndNormalize("E-R"))
                .isInstanceOf(InvalidCurrencyException.class)
                .hasMessage("Currency must contain only letters");
    }

    @Test
    @DisplayName("Should throw InvalidCurrencyException when currency contains whitespace in the middle")
    void shouldThrowWhenCurrencyContainsWhitespaceInMiddle() {
        assertThatThrownBy(() -> currencyValidator.validateAndNormalize("U SD"))
                .isInstanceOf(InvalidCurrencyException.class)
                .hasMessage("Currency must have exactly 3 characters");
    }
}