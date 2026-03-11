package pt.rho.exchangerate.validation;

import pt.rho.exchangerate.exception.InvalidCurrencyException;
import org.springframework.stereotype.Component;

@Component
public class CurrencyValidator {

    public void validate(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new InvalidCurrencyException("Currency must not be blank");
        }

        String normalizedCurrency = currency.trim();

        if (normalizedCurrency.length() != 3) {
            throw new InvalidCurrencyException("Currency must have exactly 3 characters");
        }

        if (!normalizedCurrency.matches("[a-zA-Z]{3}")) {
            throw new InvalidCurrencyException("Currency must contain only letters");
        }
    }

    public String normalizeAndValidate(String currency) {
        validate(currency);
        return currency.trim().toUpperCase();
    }
}