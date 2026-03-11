package pt.rho.exchangerate.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Conversion response for a single target currency")
public class ConversionResponse {

    @Schema(description = "Source currency", example = "USD")
    private String from;

    @Schema(description = "Target currency", example = "EUR")
    private String to;

    @Schema(description = "Original amount to convert", example = "100.00")
    private BigDecimal originalAmount;

    @Schema(description = "Exchange rate applied", example = "0.923400")
    private BigDecimal rate;

    @Schema(description = "Converted amount", example = "92.340000")
    private BigDecimal convertedAmount;

    public ConversionResponse(String from,
                              String to,
                              BigDecimal originalAmount,
                              BigDecimal rate,
                              BigDecimal convertedAmount) {
        this.from = from;
        this.to = to;
        this.originalAmount = originalAmount;
        this.rate = rate;
        this.convertedAmount = convertedAmount;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }
}