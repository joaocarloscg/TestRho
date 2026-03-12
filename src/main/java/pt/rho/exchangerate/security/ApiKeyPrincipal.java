package pt.rho.exchangerate.security;

import java.util.List;

public record ApiKeyPrincipal(String name, List<String> authorities) {
}
