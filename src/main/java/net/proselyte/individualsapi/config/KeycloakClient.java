package net.proselyte.individualsapi.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class KeycloakClient {

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakConfig.getServerUrl())
                .realm(keycloakConfig.getRealm())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(keycloakConfig.getClientId())
                .clientSecret(keycloakConfig.getClientSecret())
                .username(keycloakConfig.getUserName())
                .password(keycloakConfig.getPassword())
                .build();
    }
}
