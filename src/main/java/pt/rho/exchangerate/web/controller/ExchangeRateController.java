package pt.rho.exchangerate.web.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import pt.rho.exchangerate.dto.ConversionResponse;
import pt.rho.exchangerate.dto.ExchangeRateResponse;
import pt.rho.exchangerate.dto.ExchangeRatesResponse;
import pt.rho.exchangerate.dto.MultiConversionResponse;
import pt.rho.exchangerate.mapper.ApiResponseMapper;
import pt.rho.exchangerate.dto.ErrorResponse;
import pt.rho.exchangerate.model.ConversionResult;
import pt.rho.exchangerate.model.ExchangeRateResult;
import pt.rho.exchangerate.model.ExchangeRates;
import pt.rho.exchangerate.model.MultiConversionResult;
import pt.rho.exchangerate.service.ExchangeRateService;

@Tag(name = "Exchange Rates", description = "Operations for exchange rates and currency conversion")
@RestController
@RequestMapping("/api/v1/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;
    private final ApiResponseMapper apiResponseMapper;

    public ExchangeRateController(ExchangeRateService exchangeRateService,
                                  ApiResponseMapper apiResponseMapper) {
        this.exchangeRateService = exchangeRateService;
        this.apiResponseMapper = apiResponseMapper;
    }

	@Operation(summary = "Get all exchange rates from a base currency", description = "Returns all available exchange rates using the supplied base currency")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Exchange rates retrieved successfully", content = @Content(schema = @Schema(implementation = ExchangeRatesResponse.class))),
			@ApiResponse(responseCode = "400", description = "Invalid base currency", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Unexpected internal error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "External provider error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping("/{base}")
	public ExchangeRatesResponse getAllRates(
			@Parameter(description = "Base currency code, e.g. USD or EUR") @PathVariable String base) {
		ExchangeRates exchangeRates = exchangeRateService.getAllRates(base);

		return apiResponseMapper.toExchangeRatesResponse(exchangeRates);
	}

	@Operation(summary = "Get exchange rate from one currency to another", description = "Returns the exchange rate between two supplied currencies")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Exchange rate retrieved successfully", content = @Content(schema = @Schema(implementation = ExchangeRateResponse.class))),
			@ApiResponse(responseCode = "400", description = "Invalid currency supplied", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Exchange rate not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Unexpected internal error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "External provider error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping("/{from}/{to}")
	public ExchangeRateResponse getExchangeRate(
			@Parameter(description = "Source currency code, e.g. USD") @PathVariable String from,
			@Parameter(description = "Target currency code, e.g. EUR") @PathVariable String to) {

		ExchangeRateResult exchangeRateResult = exchangeRateService.getExchangeRate(from, to);

		return apiResponseMapper.toExchangeRateResponse(exchangeRateResult);
	}

	@Operation(summary = "Convert an amount from one currency to another", description = "Returns the exchange rate and the converted amount for a single target currency")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Conversion performed successfully", content = @Content(schema = @Schema(implementation = ConversionResponse.class))),
			@ApiResponse(responseCode = "400", description = "Invalid currency or amount", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Exchange rate not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Unexpected internal error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "External provider error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping("/convert/{from}/{to}")
	public ConversionResponse convert(
			@Parameter(description = "Source currency code, e.g. USD") @PathVariable String from,
			@Parameter(description = "Target currency code, e.g. EUR") @PathVariable String to,
			@Parameter(description = "Amount to convert", example = "100") @RequestParam BigDecimal amount) {

		ConversionResult conversionResult = exchangeRateService.convert(from, to, amount);

		return apiResponseMapper.toConversionResponse(conversionResult);
	}

	@Operation(summary = "Convert an amount from one currency to multiple currencies", description = "Returns conversion results for a list of supplied target currencies")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Multiple conversions performed successfully", content = @Content(schema = @Schema(implementation = MultiConversionResponse.class))),
			@ApiResponse(responseCode = "400", description = "Invalid source currency, target currencies or amount", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "One or more exchange rates not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "Unexpected internal error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "502", description = "External provider error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@GetMapping("/convert/{from}")
	public MultiConversionResponse convertMultiple(
			@Parameter(description = "Source currency code, e.g. USD") @PathVariable String from,
			@Parameter(description = "Amount to convert", example = "100") @RequestParam BigDecimal amount,
			@Parameter(description = "List of target currencies, e.g. EUR, GBP, JPY") @RequestParam List<String> to) {

		MultiConversionResult multiConversionResult = exchangeRateService.convert(from, to, amount);

		return apiResponseMapper.toMultiConversionResponse(multiConversionResult);
	}
}