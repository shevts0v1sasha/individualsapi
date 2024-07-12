package net.proselyte.individualsapi.service;

import net.proselyte.individualsapi.common.dto.AuthRequest;
import net.proselyte.individualsapi.common.dto.KeycloakUserDto;
import net.proselyte.individualsapi.common.dto.LoginResponse;
import net.proselyte.individualsapi.config.KeycloakConfig;
import net.proselyte.individualsapi.exception.KeycloakBadRequestException;
import net.proselyte.individualsapi.exception.KeycloakUserNotFoundException;
import net.proselyte.individualsapi.exception.NotAuthorizedException;
import net.proselyte.individualsapi.utils.DataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KeycloakServiceTests {

    @Mock
    private UsersResource usersResource;

    @Mock
    private UserResource userResource;
    @Mock
    private RoleMappingResource roleMappingResource;
    @Mock
    private RoleScopeResource roleScopeResource;

    @Mock
    private ClientsResource clientsResource;

    @Mock
    private ClientRepresentation clientRepresentation;
    @Mock
    private ClientResource clientResource;
    @Mock
    private RolesResource rolesResource;
    @Mock
    private RoleResource roleResource;
    @Mock
    private RoleRepresentation roleRepresentation;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private KeycloakConfig keycloakConfig;

    @InjectMocks
    private KeycloakService keycloakService;

    @Test
    @DisplayName("Register user functionality")
    public void givenCreateUserRequest_whenRegister_thenUserCreatedReturned() {
        //given
        String username = "testuser";
        String password = "password";
        String firstName = "Spider";
        String lastName = "Man";
        String email = "hero@gmail.com";
        BDDMockito.given(usersResource.create(any(UserRepresentation.class)))
                .willReturn(DataUtils.getResponse(HttpStatus.CREATED));
        BDDMockito.given(usersResource.search(anyString()))
                .willReturn(List.of(DataUtils.getUserRepresentation(UUID.randomUUID().toString(), username, firstName, lastName, email)));

        BDDMockito.given(clientsResource.findByClientId(anyString()))
                .willReturn(List.of(clientRepresentation));

        BDDMockito.given(clientsResource.get(anyString()))
                .willReturn(clientResource);
        BDDMockito.given(clientResource.roles())
                .willReturn(rolesResource);
        BDDMockito.given(rolesResource.get(anyString()))
                .willReturn(roleResource);
        BDDMockito.given(roleResource.toRepresentation())
                .willReturn(roleRepresentation);

        BDDMockito.given(usersResource.get(anyString()))
                .willReturn(userResource);
        BDDMockito.given(userResource.roles())
                .willReturn(roleMappingResource);
        BDDMockito.given(roleMappingResource.clientLevel(anyString()))
                .willReturn(roleScopeResource);

        //when
        KeycloakUserDto keycloakUserDto = keycloakService.register(username, password, firstName, lastName, email).block();

        //then
        assertThat(keycloakUserDto.getUsername()).isEqualTo(username);
        assertThat(keycloakUserDto.getFirstName()).isEqualTo(firstName);
        assertThat(keycloakUserDto.getLastName()).isEqualTo(lastName);
    }

    @Test
    @DisplayName("Try to register with bad request")
    public void givenBadCreateUserRequest_whenRegister_exceptionOccurred() {
        //given
        String username = "testuser";
        String password = "password";
        String firstName = "Spider";
        String lastName = "Man";
        String email = "hero@gmail.com";
        BDDMockito.given(usersResource.create(any(UserRepresentation.class)))
                .willReturn(DataUtils.getResponse(HttpStatus.BAD_REQUEST));

        //when
        assertThrows(KeycloakBadRequestException.class, () -> keycloakService.register(username, password, firstName, lastName, email).block());

        //then
        verify(usersResource, never()).search(anyString());
    }

    @Test
    @DisplayName("Get existing user by username functionality")
    public void givenExistingUsername_whenGetUserByUsername_thenUserObtained() {
        //given
        String username = "testuser";
        String firstName = "Spider";
        String lastName = "Man";
        String email = "hero@gmail.com";
        BDDMockito.given(usersResource.search(anyString()))
                .willReturn(List.of(DataUtils.getUserRepresentation(
                        UUID.randomUUID().toString(),
                        username,
                        firstName,
                        lastName,
                        email
                        )));

        //when
        KeycloakUserDto keycloakUserDto = keycloakService.getUserByUsername(username).block();

        //then
        assertThat(keycloakUserDto.getUsername()).isEqualTo(username);
        assertThat(keycloakUserDto.getFirstName()).isEqualTo(firstName);
        assertThat(keycloakUserDto.getLastName()).isEqualTo(lastName);
        assertThat(keycloakUserDto.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Get NOT existing user by username functionality")
    public void givenNotExistingUsername_whenGetUserByUsername_thenUserObtained() {
        //given
        String username = "testuser";
        BDDMockito.given(usersResource.search(anyString()))
                .willReturn(List.of());

        //when
        assertThrows(KeycloakUserNotFoundException.class, () -> keycloakService.getUserByUsername(username));
    }

    @Test
    @DisplayName("Login functionality")
    public void givenAuthRequest_whenLogin_thenTokenReturned() {
        //given
        String token = UUID.randomUUID().toString();
        BDDMockito.given(webClient.post())
                .willReturn(requestBodyUriSpec);
        BDDMockito.given(requestBodyUriSpec.uri(anyString()))
                .willReturn(requestBodyUriSpec);
        BDDMockito.given(requestBodyUriSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .willReturn(requestBodyUriSpec);
        BDDMockito.given(requestBodyUriSpec.body(any()))
                .willReturn(requestHeadersSpec);
        BDDMockito.given(requestHeadersSpec.exchangeToMono(any()))
                .willReturn(Mono.just(new LoginResponse(
                        token, 1000, 1000, token, "Bearer ")));
        BDDMockito.given(keycloakConfig.getServerUrl())
                .willReturn("");
        AuthRequest authRequest = new AuthRequest("Spider-Man", "secretpass");
        //when
        LoginResponse login = keycloakService.login(authRequest).block();
        //then
        assertThat(login.getAccessToken()).isEqualTo(token);
        assertThat(login.getRefreshToken()).isEqualTo(token);
        assertThat(login.getExpiresIn()).isEqualTo(1000);
        assertThat(login.getRefreshExpiresIn()).isEqualTo(1000);
    }

    @Test
    @DisplayName("Login with wrong login/password functionality")
    public void givenBadAuthRequest_whenLogin_thenUnauthorizedExceptionThrown() {
        //given
        BDDMockito.given(keycloakConfig.getServerUrl())
                .willReturn("");
        BDDMockito.given(webClient.post())
                .willReturn(requestBodyUriSpec);
        BDDMockito.given(requestBodyUriSpec.uri(anyString()))
                .willReturn(requestBodyUriSpec);
        BDDMockito.given(requestBodyUriSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .willReturn(requestBodyUriSpec);
        BDDMockito.given(requestBodyUriSpec.body(any()))
                .willReturn(requestHeadersSpec);
        BDDMockito.given(requestHeadersSpec.exchangeToMono(any()))
                .willReturn(Mono.error(new NotAuthorizedException("Wrong username or password")));
        AuthRequest authRequest = new AuthRequest("Spider-Man", "secretpass");
        //when
        assertThrows(NotAuthorizedException.class, () -> keycloakService.login(authRequest).block());
    }
}
