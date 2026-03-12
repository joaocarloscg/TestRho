package pt.rho.exchangerate.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1L;
	private final ApiKeyPrincipal principal;
    private final String credentials;

    public ApiKeyAuthenticationToken(String credentials) {
        super(Collections.emptyList());
        this.principal = null;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    public ApiKeyAuthenticationToken(
            ApiKeyPrincipal principal,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}