package pt.rho.exchangerate.graphql;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import pt.rho.exchangerate.dto.ConversionResponse;
import pt.rho.exchangerate.dto.ExchangeRateResponse;
import pt.rho.exchangerate.dto.MultiConversionResponse;
import pt.rho.exchangerate.graphql.dto.CurrencyRateGraphQlResponse;
import pt.rho.exchangerate.graphql.dto.ExchangeRatesGraphQlResponse;
import pt.rho.exchangerate.model.ExchangeRates;

@Component
public class GraphQLResponseMapper {

    public ExchangeRatesGraphQlResponse toExchangeRatesGraphQlResponse(ExchangeRates exchangeRates,
            List<String> currenciesFilter) {

        Stream<Map.Entry<String, java.math.BigDecimal>> rateStream = exchangeRates.getRates().entrySet().stream();

        if (currenciesFilter != null && !currenciesFilter.isEmpty()) {
            List<String> normalizedFilters = currenciesFilter.stream()
                    .map(String::toUpperCase)
                    .distinct()
                    .toList();
            rateStream = rateStream.filter(entry -> normalizedFilters.contains(entry.getKey()));
        }

        List<CurrencyRateGraphQlResponse> rates = rateStream
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .map(entry -> new CurrencyRateGraphQlResponse(entry.getKey(), entry.getValue()))
                .toList();

        return new ExchangeRatesGraphQlResponse(exchangeRates.getBaseCurrency(), rates);
    }

    public ExchangeRateResponse toExchangeRateResponse(pt.rho.exchangerate.model.ExchangeRateResult result) {
        return new ExchangeRateResponse(result.getFromCurrency(), result.getToCurrency(), result.getRate());
    }

    public ConversionResponse toConversionResponse(pt.rho.exchangerate.model.ConversionResult result) {
        return new ConversionResponse(result.getFromCurrency(), result.getToCurrency(), result.getOriginalAmount(),
                result.getRate(), result.getConvertedAmount());
    }

    public MultiConversionResponse toMultiConversionResponse(pt.rho.exchangerate.model.MultiConversionResult result) {
        return new MultiConversionResponse(
                result.getFromCurrency(),
                result.getOriginalAmount(),
                result.getConversions().stream()
                        .map(conversion -> new pt.rho.exchangerate.dto.SingleConversionItemResponse(
                                conversion.getToCurrency(),
                                conversion.getRate(),
                                conversion.getConvertedAmount()))
                        .toList());
    }
}
