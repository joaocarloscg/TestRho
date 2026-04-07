package pt.rho.exchangerate.web.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import pt.rho.exchangerate.dto.ConversionResponse;
import pt.rho.exchangerate.dto.ExchangeRateResponse;
import pt.rho.exchangerate.dto.ExchangeRatesGraphQlResponse;
import pt.rho.exchangerate.dto.MultiConversionResponse;
import pt.rho.exchangerate.mapper.GraphQLResponseMapper;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.service.ExchangeRateService;
import pt.rho.exchangerate.web.validation.CurrencyCode;

@Controller
@RequiredArgsConstructor
@Validated
public class ExchangeRateGraphQlController {

    private final ExchangeRateService exchangeRateService;
    private final GraphQLResponseMapper graphQLResponseMapper;

    @QueryMapping
    public ExchangeRatesGraphQlResponse exchangeRates(@Argument @CurrencyCode String base, @Argument List<@CurrencyCode String> currencies) {
    	base = base.trim().toUpperCase();
    	
        ExchangeRates exchangeRates = exchangeRateService.getAllRates(base);
        return graphQLResponseMapper.toExchangeRatesGraphQlResponse(exchangeRates, currencies);
    }

    @QueryMapping
    public ExchangeRateResponse exchangeRate(@Argument @CurrencyCode String from, @Argument @CurrencyCode String to) {
    	from = from.trim().toUpperCase();
    	to = to.trim().toUpperCase();
    	
        return graphQLResponseMapper.toExchangeRateResponse(exchangeRateService.getExchangeRate(from, to));
    }

    @QueryMapping
    public ConversionResponse convert(@Argument @CurrencyCode String from, @Argument @CurrencyCode String to, @Argument BigDecimal amount) {
    	from = from.trim().toUpperCase();
    	to = to.trim().toUpperCase();
    	
        return graphQLResponseMapper.toConversionResponse(exchangeRateService.convert(from, to, amount));
    }

    @QueryMapping
    public MultiConversionResponse convertMultiple(@Argument @CurrencyCode String from, @Argument List<@CurrencyCode String> to,
            @Argument BigDecimal amount) {
    	from = from.trim().toUpperCase();
    	to = to.stream().map(x -> x.trim().toUpperCase()).toList();
    	
        return graphQLResponseMapper.toMultiConversionResponse(exchangeRateService.convert(from, to, amount));
    }
}
