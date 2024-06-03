package net.proselyte.individualsapi.config;

import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class KeycloakTestContainerConfig {

    public KeycloakContainer keycloakContainer() {

    }
}
