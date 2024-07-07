package net.proselyte.individualsapi.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.proselyte.individualsapi.config.KeycloakConfig;
import net.proselyte.individualsapi.dto.*;
import net.proselyte.individualsapi.exception.KeycloakBadRequestException;
import net.proselyte.individualsapi.exception.KeycloakUserNotFoundException;
import net.proselyte.individualsapi.exception.NotAuthorizedException;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.userprofile.config.UPConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final WebClient webClient;

    private final KeycloakConfig keycloakConfig;
    private final UsersResource usersResource;
    private final Keycloak keycloak;

    public Mono<IndividualDto> register(String username, String password, String firstName, String lastName, String email) {
        CredentialRepresentation credential = createPasswordCredentials(password);

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setEmailVerified(true);
        user.setCredentials(Collections.singletonList(credential));
        user.setEnabled(true);

        try (Response response = usersResource.create(user)) {
            if (response.getStatus() == HttpStatus.CREATED.value()) {

                String createdId = CreatedResponseUtil.getCreatedId(response);

                keycloak.realm("hypercore")
                        .clients()
                        .findByClientId("hypercore")
                        .forEach(clientRepresentation -> {
                            RoleRepresentation representation = keycloak.realm("hypercore").clients()
                                    .get(clientRepresentation.getId()).roles().get("client_user").toRepresentation();
                            keycloak.realm("hypercore")
                                    .users()
                                    .get(createdId)
                                    .roles().clientLevel(clientRepresentation.getId())
                                    .add(List.of(representation));
                        });

                return getUserByUsername(username);
            } else {
                log.error("Couldn't create user in keycloak. Keycloak response status: {}", response.getStatus());
                KeycloakErrorDto keycloakErrorDto = response.readEntity(KeycloakErrorDto.class);
                throw new KeycloakBadRequestException(keycloakErrorDto.errorMessage());
            }
        }
    }

    public Mono<KeycloakLoginResponse> login(AuthRequest request) {
        return webClient.post()
                .uri("%s/realms/hypercore/protocol/openid-connect/token".formatted(keycloakConfig.getServerUrl()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("username", request.getUsername())
                        .with("password", request.getPassword())
                        .with("grant_type", "password")
                        .with("client_id", keycloakConfig.getClientId())
                        .with("client_secret", keycloakConfig.getClientSecret()))
                .exchangeToMono(clientResponse -> {
                    System.out.println(clientResponse.statusCode());
                    if (clientResponse.statusCode().value() == HttpStatus.OK.value()) {
                        return clientResponse.bodyToMono(KeycloakLoginResponse.class);
                    } else {
                        return Mono.error(new NotAuthorizedException("Wrong username or password"));
                    }
                });
    }

    public Mono<IndividualDto> getUserByUsername(String username) {
        List<UserRepresentation> search = usersResource.search(username);
        if (search.size() == 1) {
            UserRepresentation searchingUser = search.getFirst();

            return Mono.just(IndividualDto.builder()
                            .id(searchingUser.getId())
                            .username(searchingUser.getUsername())
                            .firstName(searchingUser.getFirstName())
                            .lastName(searchingUser.getLastName())
                            .email(searchingUser.getEmail())
                    .build());
        }
        throw new KeycloakUserNotFoundException("User with username=%s not found".formatted(username));
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }
}
