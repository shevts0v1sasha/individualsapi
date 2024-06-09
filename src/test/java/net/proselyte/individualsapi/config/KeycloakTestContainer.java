package net.proselyte.individualsapi.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@Configuration
@Profile("test")
public class KeycloakTestContainer {

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Bean
    public GenericContainer<?> keycloakContainer() {
        GenericContainer container = new GenericContainer("quay.io/keycloak/keycloak:24.0.4")
                .withCommand("start-dev --import-realm")
                .withExposedPorts(8080)
                .withEnv("KEYCLOAK_ADMIN", "admin")
                .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
                .withClasspathResourceMapping("realm-export.json", "/opt/keycloak/data/import/realm.json", BindMode.READ_ONLY)
                .waitingFor(Wait.defaultWaitStrategy());
        return container;
    }

    @Bean
    public Keycloak keycloak(GenericContainer<?> keycloakContainer) {
        String keycloakHost = "http://" + keycloakContainer.getContainerIpAddress() + ":" + keycloakContainer.getMappedPort(8080);
        keycloakConfig.setServerUrl(keycloakHost);
        keycloakConfig.setClientSecret("**********");
        return KeycloakBuilder.builder()
                .serverUrl(keycloakHost)
                .realm(keycloakConfig.getRealm())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(keycloakConfig.getClientId())
                .clientSecret(keycloakConfig.getClientSecret())
                .username(keycloakConfig.getUserName())
                .password(keycloakConfig.getPassword())
                .build();
    }
}
