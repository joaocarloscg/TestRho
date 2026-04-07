package pt.rho.exchangerate.provider.dto;

import lombok.Data;

@Data
public class RatesProviderResponseError {

	private Integer code;
	private String info;
}