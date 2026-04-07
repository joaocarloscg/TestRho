package pt.rho.exchangerate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "rate-limit")
@Data
public class RateLimitProperties {

	private boolean enabled = true;
	private long capacity = 60;
	private long refillTokens = 60;
	private long refillDurationMinutes = 1;
	private long bucketCacheMaxSize = 10_000;
	private long bucketCacheExpireAfterAccessMinutes = 60;

}