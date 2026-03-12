package pt.rho.exchangerate.mapper;

import org.springframework.stereotype.Component;

import pt.rho.exchangerate.dto.ConversionResponse;
import pt.rho.exchangerate.dto.ExchangeRateResponse;
import pt.rho.exchangerate.dto.ExchangeRatesResponse;
import pt.rho.exchangerate.dto.MultiConversionResponse;
import pt.rho.exchangerate.dto.SingleConversionItemResponse;
import pt.rho.exchangerate.model.ConversionResult;
import pt.rho.exchangerate.model.ExchangeRateResult;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.model.MultiConversionResult;

@Component
public class ApiResponseMapper {

    public ExchangeRatesResponse toExchangeRatesResponse(ExchangeRates exchangeRates) {
        return new ExchangeRatesResponse(
                exchangeRates.getBaseCurrency(),
                exchangeRates.getRates()
        );
    }

    public ExchangeRateResponse toExchangeRateResponse(ExchangeRateResult exchangeRateResult) {
        return new ExchangeRateResponse(
                exchangeRateResult.getFromCurrency(),
                exchangeRateResult.getToCurrency(),
                exchangeRateResult.getRate()
        );
    }

    public ConversionResponse toConversionResponse(ConversionResult conversionResult) {
        return new ConversionResponse(
                conversionResult.getFromCurrency(),
                conversionResult.getToCurrency(),
                conversionResult.getOriginalAmount(),
                conversionResult.getRate(),
                conversionResult.getConvertedAmount()
        );
    }

    public SingleConversionItemResponse toSingleConversionItemResponse(ConversionResult conversionResult) {
        return new SingleConversionItemResponse(
                conversionResult.getToCurrency(),
                conversionResult.getRate(),
                conversionResult.getConvertedAmount()
        );
    }
    
    public MultiConversionResponse toMultiConversionResponse(MultiConversionResult multiConversionResult) {
        return new MultiConversionResponse(
                multiConversionResult.getFromCurrency(),
                multiConversionResult.getOriginalAmount(),
                multiConversionResult.getConversions().stream()
                        .map(this::toSingleConversionItemResponse)
                        .toList()
        );
    }
}