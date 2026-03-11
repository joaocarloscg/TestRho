package pt.rho.exchangerate.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Single conversion item in a multi-conversion response")
public class SingleConversionItemResponse {

    @Schema(description = "Target currency", example = "EUR")
    private String currency;

    @Schema(description = "Exchange rate applied", example = "0.923400")
    private BigDecimal rate;

    @Schema(description = "Converted amount", example = "92.340000")
    private BigDecimal convertedAmount;

    public SingleConversionItemResponse(String currency, BigDecimal rate, BigDecimal convertedAmount) {
        this.currency = currency;
        this.rate = rate;
        this.convertedAmount = convertedAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }
}