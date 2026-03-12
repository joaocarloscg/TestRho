package pt.rho.exchangerate.validation;

import org.springframework.stereotype.Component;

import pt.rho.exchangerate.exception.InvalidCurrencyException;

@Component
public class CurrencyValidator {

	public String validateAndNormalize(String currency) {
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

		return currency.trim().toUpperCase();
	}
}