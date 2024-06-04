package net.proselyte.individualsapi.it;

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
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItAuthRestControllerV1Tests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Create user functionality")
    public void givenCreateNonExistentUserRequest_whenRegister_userInKeycloakCreated() {
        CreateUserRequest request = new CreateUserRequest("testuser", "password", "John", "Snow", "john.show@gmail.com");
        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserDto.class)
                .exchange();

        response.expectStatus().isCreated()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.username").isEqualTo("testuser");

    }
}
