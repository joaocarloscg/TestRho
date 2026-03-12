package pt.rho.exchangerate.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import pt.rho.exchangerate.security.ApiKeyAuthenticationFilter;
import pt.rho.exchangerate.security.ApiKeyAuthenticationProvider;
import pt.rho.exchangerate.security.JsonAccessDeniedHandler;
import pt.rho.exchangerate.security.JsonAuthenticationEntryPoint;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String RATES_READ_AUTHORITY = "SCOPE_rates.read";
    private static final String GRAPHQL_READ_AUTHORITY = "SCOPE_graphql.read";

    @Bean
    AuthenticationManager authenticationManager(ApiKeyAuthenticationProvider apiKeyAuthenticationProvider) {
        return new ProviderManager(List.of(apiKeyAuthenticationProvider));
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
            AuthenticationManager authenticationManager,
            AuthProperties authProperties,
            JsonAuthenticationEntryPoint authenticationEntryPoint,
            JsonAccessDeniedHandler accessDeniedHandler) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler));

        if (!authProperties.isEnabled()) {
            http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
            return http.build();
        }

        ApiKeyAuthenticationFilter apiKeyAuthenticationFilter = new ApiKeyAuthenticationFilter(
                authenticationManager,
                authenticationEntryPoint,
                authProperties.getHeaderName());

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/graphiql", "/graphiql/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/exchange-rates/**").hasAuthority(RATES_READ_AUTHORITY)
                        .requestMatchers(HttpMethod.POST, "/graphql").hasAuthority(GRAPHQL_READ_AUTHORITY)
                        .requestMatchers(HttpMethod.GET, "/graphql").hasAuthority(GRAPHQL_READ_AUTHORITY)
                        .anyRequest().authenticated())
                .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
