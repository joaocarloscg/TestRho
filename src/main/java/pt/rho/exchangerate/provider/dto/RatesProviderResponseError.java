package pt.rho.exchangerate.provider.dto;

public class RatesProviderResponseError {

	private Integer code;
	private String info;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}