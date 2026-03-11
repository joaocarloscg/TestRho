package pt.rho.exchangerate.controller;

import pt.rho.exchangerate.client.ExchangeRateClient;
import pt.rho.exchangerate.dto.external.ExchangeRateHostLatestResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private final ExchangeRateClient exchangeRateClient;

    public TestController(ExchangeRateClient exchangeRateClient) {
        this.exchangeRateClient = exchangeRateClient;
    }

    @GetMapping("/latest")
    public ExchangeRateHostLatestResponse getLatestRates(
            @RequestParam(defaultValue = "EUR") String base) {
        return exchangeRateClient.getLatestRates(base);
    }
}