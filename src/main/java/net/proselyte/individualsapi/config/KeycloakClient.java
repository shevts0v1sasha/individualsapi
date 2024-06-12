package net.proselyte.individualsapi.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakClient {

    @Bean
    public Keycloak keycloak(KeycloakConfig keycloakConfig) {
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

    @Bean
    public UsersResource usersResource(KeycloakConfig config,
                                       Keycloak keycloak) {
        return keycloak.realm(config.getRealm()).users();
    }
}
