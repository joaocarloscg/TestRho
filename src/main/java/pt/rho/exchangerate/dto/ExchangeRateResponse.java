package pt.rho.exchangerate.dto;

import java.math.BigDecimal;

public class ExchangeRateResponse {

    private String from;
    private String to;
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