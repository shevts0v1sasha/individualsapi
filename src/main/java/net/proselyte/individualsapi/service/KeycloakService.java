package net.proselyte.individualsapi.service;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.proselyte.individualsapi.dto.*;
import net.proselyte.individualsapi.exception.NotAuthorizedException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final Keycloak keycloak;
    private final WebClient webClient;

    @Value("${keycloak.realm}")
    private String realm;
    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    public Mono<UserDto> register(CreateUserRequest request) {
        CredentialRepresentation credential = createPasswordCredentials(request.password());
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setEmailVerified(true);
        user.setCredentials(Collections.singletonList(credential));
        user.setEnabled(true);
        UsersResource usersResource = getUsersResource();

        try (Response response = usersResource.create(user)) {
            if (response.getStatus() == HttpStatus.CREATED.value()) {
                return getUserByUsername(request.username());
            } else {
                KeycloakErrorDto keycloakErrorDto = response.readEntity(KeycloakErrorDto.class);
                throw new BadRequestException(keycloakErrorDto.errorMessage());
            }
        }
    }

    public Mono<KeycloakLoginResponse> login(AuthRequest request) {
        return webClient.post()
                .uri("%s/realms/hypercore/protocol/openid-connect/token".formatted(keycloakServerUrl))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("username", request.getUsername())
                        .with("password", request.getPassword())
                        .with("grant_type", "password")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().value() == HttpStatus.OK.value()) {
                        return clientResponse.bodyToMono(KeycloakLoginResponse.class);
                    } else {
                        return Mono.error(new NotAuthorizedException("Wrong username or password"));
                    }
                });
    }

    public Mono<UserDto> getUserByUsername(String username) {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> search = usersResource.search(username);
        if (search.size() == 1) {
            UserRepresentation searchingUser = search.get(0);

            return Mono.just(UserDto.builder()
                            .id(searchingUser.getId())
                            .username(searchingUser.getUsername())
                            .firstName(searchingUser.getFirstName())
                            .lastName(searchingUser.getLastName())
                            .email(searchingUser.getEmail())
                    .build());
        }
        throw new NotFoundException("User with username=%s not found".formatted(username));
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }
}
