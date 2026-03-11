package pt.rho.exchangerate.controller;

import pt.rho.exchangerate.dto.ExchangeRateResponse;
import pt.rho.exchangerate.dto.ExchangeRatesResponse;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.service.ExchangeRateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/{base}")
    public ExchangeRatesResponse getAllRates(@PathVariable String base) {
        ExchangeRates exchangeRates = exchangeRateService.getAllRates(base);

        return new ExchangeRatesResponse(
                exchangeRates.getBaseCurrency(),
                exchangeRates.getRates()
        );
    }

    @GetMapping("/{from}/{to}")
    public ExchangeRateResponse getExchangeRate(
            @PathVariable String from,
            @PathVariable String to) {

        BigDecimal rate = exchangeRateService.getExchangeRate(from, to);

        return new ExchangeRateResponse(
                from.trim().toUpperCase(),
                to.trim().toUpperCase(),
                rate
        );
    }
}