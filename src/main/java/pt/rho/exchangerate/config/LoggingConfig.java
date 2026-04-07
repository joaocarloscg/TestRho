package pt.rho.exchangerate.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class LoggingConfig {
	@Bean
	ApplicationRunner applicationLoggingConfig(ApplicationProperties applicationProperties) {
		return args -> {
			boolean hasAccessKey = applicationProperties.getAccessKey() != null 
					&& !applicationProperties.getAccessKey().isBlank();

			log.info("Exchange rate provider base URL: {}", applicationProperties.getBaseUrl());
			log.info("Exchange rate provider timeoutSeconds: {}", applicationProperties.getTimeoutSeconds());
			log.info("Exchange rate provider access key configured: {}", hasAccessKey);
		};
	}
}