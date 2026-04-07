package pt.rho.exchangerate.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "auth")
@Data
public class AuthProperties {

    private boolean enabled = true;
    private String headerName = "X-API-Key";
    private List<Client> clients = new ArrayList<>();

    @Data
    public static class Client {
        private String name;
        private String key;
        private List<String> authorities = new ArrayList<>();
    }
}
