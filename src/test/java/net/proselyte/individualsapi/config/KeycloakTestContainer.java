package net.proselyte.individualsapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@Configuration
@Profile("test")
public class KeycloakTestContainer {

    @Bean
    @Order(1)
    public GenericContainer<?> keycloakContainer(KeycloakConfig keycloakConfig) {
        GenericContainer container = new GenericContainer("quay.io/keycloak/keycloak:24.0.4")
                .withCommand("start-dev --import-realm")
                .withExposedPorts(8080)
                .withEnv("KEYCLOAK_ADMIN", "admin")
                .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
                .withClasspathResourceMapping("realm-export.json", "/opt/keycloak/data/import/realm.json", BindMode.READ_ONLY)
                .waitingFor(Wait.defaultWaitStrategy());

        container.start();
        String keycloakHost = "http://" + container.getContainerIpAddress() + ":" + container.getMappedPort(8080);
        keycloakConfig.setServerUrl(keycloakHost);
        keycloakConfig.setClientSecret("**********");
        return container;
    }
}
