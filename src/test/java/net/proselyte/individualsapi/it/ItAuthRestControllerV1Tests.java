package net.proselyte.individualsapi.it;

import com.github.dockerjava.api.model.AuthResponse;
import net.proselyte.individualsapi.dto.AuthRequest;
import net.proselyte.individualsapi.dto.CreateUserRequest;
import net.proselyte.individualsapi.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "PT15M")
@ActiveProfiles("test")
public class ItAuthRestControllerV1Tests {

    @Autowired
    private WebTestClient webTestClient;

    private final static String testUsername = "testuser";
    private final static String testPassword = "password";

    @Test
    @DisplayName("Create user functionality")
    public void givenCreateNonExistentUserRequest_whenRegister_thenUserInKeycloakCreated() {
        //given
        CreateUserRequest request = new CreateUserRequest(testUsername, testPassword, "John", "Snow", "john.show@gmail.com");

        //when
        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserDto.class)
                .exchange();

        //then
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.username").isEqualTo(testUsername);
    }

    @Test
    @DisplayName("Login functionality")
    public void givenCreatedUsername_whenLogin_thenTokenObtained() {
        //given
        AuthRequest authRequest = new AuthRequest(testUsername, testPassword);

        //when
        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authRequest), AuthResponse.class)
                .exchange();

        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.access_token").isNotEmpty()
                .jsonPath("$.expires_in").isEqualTo(300)
                .jsonPath("$.refresh_token").isNotEmpty()
                .jsonPath("$.token_type").isEqualTo("Bearer");
    }
}
