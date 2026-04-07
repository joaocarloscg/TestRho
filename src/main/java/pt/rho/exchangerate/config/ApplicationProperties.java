package pt.rho.exchangerate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "exchange-rate.provider")
@Data
public class ApplicationProperties {

	private String baseUrl;
	private int timeoutSeconds;
	private String accessKey;

}