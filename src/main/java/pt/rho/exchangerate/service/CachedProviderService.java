package pt.rho.exchangerate.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pt.rho.exchangerate.provider.dto.RatesProviderResponse;
import pt.rho.exchangerate.mapper.RatesProviderResponseMapper;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.provider.RatesProvider;

@Service
@RequiredArgsConstructor
public class CachedProviderService {

	private final RatesProvider ratesProvider;
	private final RatesProviderResponseMapper ratesProviderResponseMapper;

	@Cacheable(cacheNames = "exchangeRates", key = "#normalizedBaseCurrency")
	public ExchangeRates getAllRates(String normalizedBaseCurrency) {
		RatesProviderResponse response = ratesProvider.getRates(normalizedBaseCurrency);

		return ratesProviderResponseMapper.toExchangeRates(normalizedBaseCurrency, response);
	}
}