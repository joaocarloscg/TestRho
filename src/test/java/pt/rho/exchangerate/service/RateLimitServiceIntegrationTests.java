package pt.rho.exchangerate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import pt.rho.exchangerate.model.ExchangeRates;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(properties = {
        "rate-limit.enabled=true",
        "rate-limit.capacity=2",
        "rate-limit.refill-tokens=2",
        "rate-limit.refill-duration-minutes=1"
})
@AutoConfigureMockMvc
class RateLimitServiceIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExchangeRateService exchangeRateService;

    @Test
    void shouldReturn429AfterRateLimitExceeded() throws Exception {
        when(exchangeRateService.getAllRates("USD"))
                .thenReturn(new ExchangeRates("USD", Map.of("EUR", BigDecimal.valueOf(0.92))));

        mockMvc.perform(get("/api/v1/exchange-rates/USD")
                        .header("X-Forwarded-For", "1.2.3.4"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/exchange-rates/USD")
                        .header("X-Forwarded-For", "1.2.3.4"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/exchange-rates/USD")
                        .header("X-Forwarded-For", "1.2.3.4"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.error").value("Too Many Requests"))
                .andExpect(jsonPath("$.message").value("Rate limit exceeded. Please try again later."))
                .andExpect(jsonPath("$.path").value("/api/v1/exchange-rates/USD"));
    }
}