package pt.rho.exchangerate.provider;

import pt.rho.exchangerate.provider.dto.RatesProviderResponse;

public interface RatesProvider {

	RatesProviderResponse getRates(String baseCurrency);
}