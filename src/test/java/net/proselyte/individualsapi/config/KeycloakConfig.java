package net.proselyte.individualsapi.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@Configuration
@Profile("test")
public class KeycloakConfig {

    @Value("${keycloak.realm}")
    private String realm;
    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;
    @Value("${keycloak.username}")
    private String userName;
    @Value("${keycloak.password}")
    private String password;

    @Bean
    public GenericContainer<?> keycloakContainer() {
        GenericContainer container = new GenericContainer("quay.io/keycloak/keycloak:24.0.4")
                .withCommand("start-dev")
                .withExposedPorts(8080)
                .withEnv("KEYCLOAK_ADMIN", "admin")
                .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
                .withCommand("import --file=/opt/keycloak/data/import/realm.json --optimized")
                .withClasspathResourceMapping("realm-export.json", "/opt/keycloak/data/import/realm.json", BindMode.READ_ONLY)
                .waitingFor(Wait.defaultWaitStrategy());
        return container;
    }

    @Bean
    public Keycloak keycloak(GenericContainer<?> keycloakContainer) {
        String keycloakHost = "http://" + keycloakContainer.getContainerIpAddress() + ":" + keycloakContainer.getMappedPort(8080);
        return KeycloakBuilder.builder()
                .serverUrl(keycloakHost)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(userName)
                .password(password)
                .build();
    }
}
