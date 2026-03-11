package pt.rho.exchangerate.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Exchange rate response between two currencies")
public class ExchangeRateResponse {

    @Schema(description = "Source currency", example = "USD")
    private String from;

    @Schema(description = "Target currency", example = "EUR")
    private String to;

    @Schema(description = "Exchange rate from source to target currency", example = "0.9234")
    private BigDecimal rate;

    public ExchangeRateResponse(String from, String to, BigDecimal rate) {
        this.from = from;
        this.to = to;
        this.rate = rate;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public BigDecimal getRate() {
        return rate;
    }
}