package net.proselyte.individualsapi.rest;

import lombok.RequiredArgsConstructor;
import net.proselyte.individualsapi.dto.*;
import net.proselyte.individualsapi.service.KeycloakService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestControllerV1 {

    private final KeycloakService keycloakService;

    @PostMapping("/register")
    public Mono<UserDto> registerUser(@RequestBody CreateUserRequest request) {
        return keycloakService.register(request);
    }

    @PostMapping("/login")
    public Mono<KeycloakLoginResponse> login(@RequestBody AuthRequest request) {
        return keycloakService.login(request);
    }

    @GetMapping("/{username}")
    public Mono<UserDto> getUserByUsername(@PathVariable String username) {
        return keycloakService.getUserByUsername(username);
    }
}
