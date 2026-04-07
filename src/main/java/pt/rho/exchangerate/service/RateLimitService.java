package pt.rho.exchangerate.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import pt.rho.exchangerate.config.RateLimitProperties;

@Service
@RequiredArgsConstructor
public class RateLimitService {

	private final RateLimitProperties rateLimitProperties;
	private Cache<String, Bucket> buckets;

    @PostConstruct
    void init() {
        this.buckets = Caffeine.newBuilder()
                .maximumSize(rateLimitProperties.getBucketCacheMaxSize())
                .expireAfterAccess(Duration.ofMinutes(rateLimitProperties.getBucketCacheExpireAfterAccessMinutes()))
                .build();
    }
    
	public boolean tryConsume(String clientKey) {
		Bucket bucket = buckets.get(clientKey, key -> newBucket());
		return bucket.tryConsume(1);
	}

	private Bucket newBucket() {
		return Bucket.builder()
				.addLimit(Bandwidth.builder()
						.capacity(rateLimitProperties.getCapacity())
						.refillIntervally(rateLimitProperties.getRefillTokens(),
								Duration.ofMinutes(rateLimitProperties.getRefillDurationMinutes()))
						.build())
				.build();
	}
}