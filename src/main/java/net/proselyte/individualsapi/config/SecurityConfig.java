package net.proselyte.individualsapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;
    private final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/individuals/register",
            "/api/v1/auth/individuals/login"
    };

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchange -> exchange.pathMatchers(PUBLIC_ENDPOINTS).permitAll()
                .pathMatchers("/api/v1/persons/individuals/info").hasAuthority("ROLE_client_user")
                .anyExchange().authenticated());
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        http
                .oauth2ResourceServer(oauth -> oauth.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthConverter)));

        return http.build();
    }


}
