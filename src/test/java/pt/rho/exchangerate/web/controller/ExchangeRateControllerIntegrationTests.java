package pt.rho.exchangerate.web.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(properties = {
        "auth.enabled=false"
})
@AutoConfigureMockMvc
class ExchangeRateControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/exchange-rates/convert/{from}/{to} should return 400 when amount is invalid")
    void shouldReturnBadRequestWhenSingleConvertAmountIsInvalidType() throws Exception {
        mockMvc.perform(get("/api/v1/exchange-rates/convert/USD/EUR")
                        .param("amount", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid value for parameter 'amount': abc"));
    }

    @Test
    @DisplayName("GET /api/v1/exchange-rates/convert/{from}/{to} should return 400 when amount is missing")
    void shouldReturnBadRequestWhenSingleConvertAmountMissing() throws Exception {
        mockMvc.perform(get("/api/v1/exchange-rates/convert/USD/EUR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Required request parameter 'amount' is missing"));
    }

    @Test
    @DisplayName("GET /api/v1/exchange-rates/convert/{from} should return 400 when amount is invalid")
    void shouldReturnBadRequestWhenMultiConvertAmountIsInvalidType() throws Exception {
        mockMvc.perform(get("/api/v1/exchange-rates/convert/USD")
                        .param("amount", "abc")
                        .param("to", "EUR", "GBP"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid value for parameter 'amount': abc"));
    }

    @Test
    @DisplayName("GET /api/v1/exchange-rates/convert/{from} should return 400 when amount is missing")
    void shouldReturnBadRequestWhenMultiConvertAmountMissing() throws Exception {
        mockMvc.perform(get("/api/v1/exchange-rates/convert/USD")
                        .param("to", "EUR", "GBP"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Required request parameter 'amount' is missing"));
    }

    @Test
    @DisplayName("GET /api/v1/exchange-rates/convert/{from} should return 400 when target currencies are missing")
    void shouldReturnBadRequestWhenMultiConvertTargetsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/exchange-rates/convert/USD")
                        .param("amount", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Required request parameter 'to' is missing"));
    }
}