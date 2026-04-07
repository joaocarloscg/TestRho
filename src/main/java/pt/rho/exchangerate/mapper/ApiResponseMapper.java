package pt.rho.exchangerate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import pt.rho.exchangerate.config.MapStructConfig;
import pt.rho.exchangerate.dto.ConversionResponse;
import pt.rho.exchangerate.dto.ExchangeRateResponse;
import pt.rho.exchangerate.dto.ExchangeRatesResponse;
import pt.rho.exchangerate.dto.MultiConversionResponse;
import pt.rho.exchangerate.dto.SingleConversionItemResponse;
import pt.rho.exchangerate.model.ConversionResult;
import pt.rho.exchangerate.model.ExchangeRateResult;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.model.MultiConversionResult;

@Mapper(config = MapStructConfig.class)
public interface ApiResponseMapper {

    @Mapping(target = "base", source = "baseCurrency")
    ExchangeRatesResponse toExchangeRatesResponse(ExchangeRates exchangeRates);

    @Mapping(target = "from", source = "fromCurrency")
    @Mapping(target = "to", source = "toCurrency")
    ExchangeRateResponse toExchangeRateResponse(ExchangeRateResult exchangeRateResult);

    @Mapping(target = "from", source = "fromCurrency")
    @Mapping(target = "to", source = "toCurrency")
    ConversionResponse toConversionResponse(ConversionResult conversionResult);

    @Mapping(target = "currency", source = "toCurrency")
    SingleConversionItemResponse toSingleConversionItemResponse(ConversionResult conversionResult);

    @Mapping(target = "from", source = "fromCurrency")
    MultiConversionResponse toMultiConversionResponse(MultiConversionResult multiConversionResult);
    
}