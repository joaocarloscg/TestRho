package pt.rho.exchangerate.model;

import java.math.BigDecimal;
import java.util.List;

public class MultiConversionResult {

    private String fromCurrency;
    private BigDecimal originalAmount;
    private List<ConversionResult> conversions;

    public MultiConversionResult(String fromCurrency,
                                 BigDecimal originalAmount,
                                 List<ConversionResult> conversions) {
        this.fromCurrency = fromCurrency;
        this.originalAmount = originalAmount;
        this.conversions = conversions;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public List<ConversionResult> getConversions() {
        return conversions;
    }
}