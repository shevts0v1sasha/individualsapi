package net.proselyte.individualsapi.it;

import com.github.dockerjava.api.model.AuthResponse;
import net.proselyte.individualsapi.config.KeycloakTestContainers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.main.allow-bean-definition-overriding=true")
@Testcontainers
@TestPropertySource(locations = "classpath:application-test.yaml")
public class ItAuthRestControllerV1Tests extends KeycloakTestContainers {

    @Autowired
    private WebTestClient webTestClient;

    private final static String testUsername = "testuser";
    private final static String testPassword = "password";

    @Test
    @DisplayName("Create user functionality")
    @Order(1)
    public void givenCreateNonExistentUserRequest_whenRegister_thenUserInKeycloakCreated() {
        //given
        IndividualDto individualDto = IndividualDto.builder()
                .username(testUsername)
                .password(testPassword)
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("ivan-ivanov@gmail.com")
                .address(new AddressDto("Russia", "Lenina st., 5", "Moskovskaya obl.", "Moscow", "620000"))
                .passportNumber("213123123")
                .phoneNumber("89005553535")
                .build();

        //when
        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/v1/auth/individuals/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(individualDto), IndividualDto.class)
                .exchange();

        //then
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.username").isEqualTo(testUsername);
    }

    @Test
    @DisplayName("Login functionality")
    @Order(2)
    public void givenCreatedUsername_whenLogin_thenTokenObtained() {
        //given
        AuthRequest authRequest = new AuthRequest(testUsername, testPassword);

        //when
        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/v1/auth/individuals/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authRequest), AuthResponse.class)
                .exchange();

        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.access_token").isNotEmpty()
                .jsonPath("$.expires_in").isEqualTo(300)
                .jsonPath("$.refresh_token").isNotEmpty()
                .jsonPath("$.token_type").isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Get individual info by jwt token functionality")
    @Order(3)
    public void givenJwtToken_whenGetInfo_thenUserInfoObtained() {
        //given
        AuthRequest authRequest = new AuthRequest(testUsername, testPassword);
        LoginResponse loginResponse = webTestClient.post()
                .uri("/api/v1/auth/individuals/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authRequest), AuthResponse.class)
                .exchange()
                .expectStatus().isOk()
                .returnResult(LoginResponse.class)
                .getResponseBody().blockFirst();

        //when
        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/v1/persons/individuals/info")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(loginResponse.getAccessToken()))
                .exchange();

        //then
        response.expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo(testUsername)
                .jsonPath("$.firstName").isEqualTo("Ivan");
    }
}
