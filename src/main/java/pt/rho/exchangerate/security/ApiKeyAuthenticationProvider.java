package pt.rho.exchangerate.security;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import pt.rho.exchangerate.config.AuthProperties;

@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    private final Map<String, AuthProperties.Client> clientsByKey;

    public ApiKeyAuthenticationProvider(AuthProperties authProperties) {
        this.clientsByKey = authProperties.getClients().stream()
                .filter(client -> StringUtils.hasText(client.getKey()))
                .collect(Collectors.toUnmodifiableMap(AuthProperties.Client::getKey, Function.identity()));
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String providedKey = (String) authentication.getCredentials();
        AuthProperties.Client client = clientsByKey.get(providedKey);

        if (client == null) {
            throw new BadCredentialsException("Invalid API key");
        }

        List<SimpleGrantedAuthority> authorities = client.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        ApiKeyPrincipal principal = new ApiKeyPrincipal(client.getName(), client.getAuthorities());
        return new ApiKeyAuthenticationToken(principal, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
