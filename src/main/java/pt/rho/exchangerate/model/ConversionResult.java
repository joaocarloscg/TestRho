package pt.rho.exchangerate.model;

import java.math.BigDecimal;

public class ConversionResult {

    private String fromCurrency;
    private String toCurrency;
    private BigDecimal originalAmount;
    private BigDecimal rate;
    private BigDecimal convertedAmount;

    public ConversionResult(String fromCurrency,
                            String toCurrency,
                            BigDecimal originalAmount,
                            BigDecimal rate,
                            BigDecimal convertedAmount) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.originalAmount = originalAmount;
        this.rate = rate;
        this.convertedAmount = convertedAmount;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
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