package pt.rho.exchangerate.graphql;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import pt.rho.exchangerate.dto.ConversionResponse;
import pt.rho.exchangerate.dto.ExchangeRateResponse;
import pt.rho.exchangerate.dto.MultiConversionResponse;
import pt.rho.exchangerate.graphql.dto.ExchangeRatesGraphQlResponse;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.service.ExchangeRateService;

@Controller
public class ExchangeRateGraphQlController {

    private final ExchangeRateService exchangeRateService;
    private final ResponseMapper responseMapper;

    public ExchangeRateGraphQlController(ExchangeRateService exchangeRateService,
            ResponseMapper responseMapper) {
        this.exchangeRateService = exchangeRateService;
        this.responseMapper = responseMapper;
    }

    @QueryMapping
    public ExchangeRatesGraphQlResponse exchangeRates(@Argument String base, @Argument List<String> currencies) {
        ExchangeRates exchangeRates = exchangeRateService.getAllRates(base);
        return responseMapper.toExchangeRatesGraphQlResponse(exchangeRates, currencies);
    }

    @QueryMapping
    public ExchangeRateResponse exchangeRate(@Argument String from, @Argument String to) {
        return responseMapper.toExchangeRateResponse(exchangeRateService.getExchangeRate(from, to));
    }

    @QueryMapping
    public ConversionResponse convert(@Argument String from, @Argument String to, @Argument BigDecimal amount) {
        return responseMapper.toConversionResponse(exchangeRateService.convert(from, to, amount));
    }

    @QueryMapping
    public MultiConversionResponse convertMultiple(@Argument String from, @Argument List<String> to,
            @Argument BigDecimal amount) {
        return responseMapper.toMultiConversionResponse(exchangeRateService.convert(from, to, amount));
    }
}
