package pt.rho.exchangerate.web;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pt.rho.exchangerate.config.RateLimitProperties;
import pt.rho.exchangerate.exception.RateLimitExceededException;
import pt.rho.exchangerate.service.RateLimitService;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

	private final RateLimitService rateLimitService;
	private final RateLimitProperties rateLimitProperties;
	private final HandlerExceptionResolver handlerExceptionResolver;

	public RateLimitingFilter(
			RateLimitService rateLimitService, 
			RateLimitProperties rateLimitProperties,
			HandlerExceptionResolver handlerExceptionResolver) {
		this.rateLimitService = rateLimitService;
		this.rateLimitProperties = rateLimitProperties;
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, 
			HttpServletResponse response, 
			FilterChain filterChain)
			throws ServletException, IOException {

		if (!rateLimitProperties.isEnabled()) {
			filterChain.doFilter(request, response);
			return;
		}

		String clientKey = resolveClientKey(request);

		if (!rateLimitService.tryConsume(clientKey)) {
			handlerExceptionResolver.resolveException(request, response, null,
					new RateLimitExceededException("Rate limit exceeded. Please try again later."));
			return;
		}

		filterChain.doFilter(request, response);
	}

	private String resolveClientKey(HttpServletRequest request) {
		String forwardedFor = request.getHeader("X-Forwarded-For");
		if (forwardedFor != null && !forwardedFor.isBlank()) {
			return forwardedFor.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return !request.getRequestURI().startsWith("/api/");
	}
}