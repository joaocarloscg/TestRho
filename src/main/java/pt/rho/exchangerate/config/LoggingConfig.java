package pt.rho.exchangerate.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {
	private static final Logger log = LoggerFactory.getLogger(LoggingConfig.class);

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