package pt.rho.exchangerate.dto;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Conversion response for multiple target currencies")
public class MultiConversionResponse {

	@Schema(description = "Source currency", example = "USD")
	private String from;

	@Schema(description = "Original amount to convert", example = "100.00")
	private BigDecimal originalAmount;

	@Schema(description = "List of conversion results")
	private List<SingleConversionItemResponse> conversions;

	public MultiConversionResponse(String from, BigDecimal originalAmount,
			List<SingleConversionItemResponse> conversions) {
		this.from = from;
		this.originalAmount = originalAmount;
		this.conversions = conversions;
	}

	public String getFrom() {
		return from;
	}

	public BigDecimal getOriginalAmount() {
		return originalAmount;
	}

	public List<SingleConversionItemResponse> getConversions() {
		return conversions;
	}
}