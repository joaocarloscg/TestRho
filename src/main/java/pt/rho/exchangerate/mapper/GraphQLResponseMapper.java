package pt.rho.exchangerate.mapper;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import pt.rho.exchangerate.config.MapStructConfig;
import pt.rho.exchangerate.dto.ConversionResponse;
import pt.rho.exchangerate.dto.CurrencyRateGraphQlResponse;
import pt.rho.exchangerate.dto.ExchangeRateResponse;
import pt.rho.exchangerate.dto.ExchangeRatesGraphQlResponse;
import pt.rho.exchangerate.dto.MultiConversionResponse;
import pt.rho.exchangerate.dto.SingleConversionItemResponse;
import pt.rho.exchangerate.model.ConversionResult;
import pt.rho.exchangerate.model.ExchangeRateResult;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.model.MultiConversionResult;

@Mapper(config = MapStructConfig.class)
public interface GraphQLResponseMapper {

    default ExchangeRatesGraphQlResponse toExchangeRatesGraphQlResponse(
            ExchangeRates exchangeRates,
            List<String> currenciesFilter) {

        Stream<Map.Entry<String, BigDecimal>> rateStream = exchangeRates.getRates().entrySet().stream();

        if (currenciesFilter != null && !currenciesFilter.isEmpty()) {
            List<String> normalizedFilters = currenciesFilter.stream()
                    .map(String::toUpperCase)
                    .distinct()
                    .toList();

            rateStream = rateStream.filter(entry -> normalizedFilters.contains(entry.getKey()));
        }

        List<CurrencyRateGraphQlResponse> rates = rateStream
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .map(this::toCurrencyRateGraphQlResponse)
                .toList();

        return new ExchangeRatesGraphQlResponse(exchangeRates.getBaseCurrency(), rates);
    }

    default CurrencyRateGraphQlResponse toCurrencyRateGraphQlResponse(Map.Entry<String, BigDecimal> entry) {
        return new CurrencyRateGraphQlResponse(entry.getKey(), entry.getValue());
    }
    
    @Mapping(target = "from", source = "fromCurrency")
    @Mapping(target = "to", source = "toCurrency")
    ExchangeRateResponse toExchangeRateResponse(ExchangeRateResult result);

    @Mapping(target = "from", source = "fromCurrency")
    @Mapping(target = "to", source = "toCurrency")
    ConversionResponse toConversionResponse(ConversionResult result);

    @Mapping(target = "from", source = "fromCurrency")
    MultiConversionResponse toMultiConversionResponse(MultiConversionResult result);

    @Mapping(target = "currency", source = "toCurrency")
    SingleConversionItemResponse toSingleConversionItemResponse(ConversionResult result);
}
