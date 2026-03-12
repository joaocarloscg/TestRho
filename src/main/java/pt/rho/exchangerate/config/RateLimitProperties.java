package pt.rho.exchangerate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

	private boolean enabled = true;
	private long capacity = 60;
	private long refillTokens = 60;
	private long refillDurationMinutes = 1;
	private long bucketCacheMaxSize = 10_000;
	private long bucketCacheExpireAfterAccessMinutes = 60;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public long getCapacity() {
		return capacity;
	}

	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}

	public long getRefillTokens() {
		return refillTokens;
	}

	public void setRefillTokens(long refillTokens) {
		this.refillTokens = refillTokens;
	}

	public long getRefillDurationMinutes() {
		return refillDurationMinutes;
	}

	public void setRefillDurationMinutes(long refillDurationMinutes) {
		this.refillDurationMinutes = refillDurationMinutes;
	}

	public long getBucketCacheMaxSize() {
		return bucketCacheMaxSize;
	}

	public void setBucketCacheMaxSize(long bucketCacheMaxSize) {
		this.bucketCacheMaxSize = bucketCacheMaxSize;
	}

	public long getBucketCacheExpireAfterAccessMinutes() {
		return bucketCacheExpireAfterAccessMinutes;
	}

	public void setBucketCacheExpireAfterAccessMinutes(long bucketCacheExpireAfterAccessMinutes) {
		this.bucketCacheExpireAfterAccessMinutes = bucketCacheExpireAfterAccessMinutes;
	}
}