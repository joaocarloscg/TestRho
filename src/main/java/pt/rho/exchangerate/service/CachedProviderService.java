package pt.rho.exchangerate.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import pt.rho.exchangerate.provider.dto.RatesProviderResponse;
import pt.rho.exchangerate.mapper.RatesProviderResponseMapper;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.provider.RatesProvider;

@Service
public class CachedProviderService {

	private final RatesProvider ratesProvider;
	private final RatesProviderResponseMapper ratesProviderResponseMapper;

	public CachedProviderService(
			RatesProvider ratesProvider,
			RatesProviderResponseMapper ratesProviderResponseMapper) {
		this.ratesProvider = ratesProvider;
		this.ratesProviderResponseMapper = ratesProviderResponseMapper;
	}

	@Cacheable(cacheNames = "exchangeRates", key = "#normalizedBaseCurrency")
	public ExchangeRates getAllRates(String normalizedBaseCurrency) {
		RatesProviderResponse response = ratesProvider.getRates(normalizedBaseCurrency);

		return ratesProviderResponseMapper.toExchangeRates(normalizedBaseCurrency, response);
	}
}