package net.proselyte.individualsapi.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class KeycloakTestContainers {

    private static final String CLIENT_ID = "hypercore";
    private static final String CLIENT_SECRET = "DOoH7QgR067e0znlXwrVg3vgFbEVD9cW";

    static final KeycloakContainer keycloak;
    static final PostgreSQLContainer<?> postgreSQLContainer;

    static {
        keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:24.0.4")
                .withRealmImportFile("realm-export.json")
                .withEnv("DB_VENDOR", "h2")
                .withEnv("DB_URL", "jdbc:h2:mem:testdb")
                .withEnv("DB_USER", "sa")
                .withEnv("DB_PASSWORD", "");
        keycloak.start();

        postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("postgres")
                .withUsername("postgres")
                .withPassword("postgres")
                .withExposedPorts(5432);
        postgreSQLContainer.start();
    }


    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> keycloak.getAuthServerUrl() + "/realms/hypercore");
        registry.add("keycloak-service.client.base-admin-url", () -> keycloak.getAuthServerUrl() + "/admin/realms/hypercore");
        registry.add("keycloak.clientId", () -> CLIENT_ID);
        registry.add("keycloak.clientSecret", () -> CLIENT_SECRET);
        registry.add("keycloak.urls.auth", () -> keycloak.getAuthServerUrl());
        registry.add("keycloak.server-url", () -> keycloak.getAuthServerUrl());

        registry.add("spring.r2dbc.url",
                () -> "r2dbc:pool:postgres://localhost:%d/postgres".formatted(postgreSQLContainer.getFirstMappedPort()));
        registry.add("spring.r2dbc.username", () -> "postgres");
        registry.add("spring.r2dbc.password", () -> "postgres");
        registry.add("spring.flyway.url", () -> "jdbc:postgresql://localhost:%d/postgres".formatted(postgreSQLContainer.getFirstMappedPort()));

    }

    @Bean
    @Primary
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloak.getAuthServerUrl())
                .realm("hypercore")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .build();
    }
}
