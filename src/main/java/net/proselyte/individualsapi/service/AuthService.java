package net.proselyte.individualsapi.service;

import lombok.RequiredArgsConstructor;
import net.proselyte.individualsapi.common.dto.AuthRequest;
import net.proselyte.individualsapi.common.dto.LoginResponse;
import net.proselyte.individualsapi.common.dto.IndividualDto;
import net.proselyte.individualsapi.entity.IndividualEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PersonService personService;
    private final KeycloakService keycloakService;

    public Mono<IndividualEntity> registerIndividual(IndividualDto individual) {
        return personService.createIndividual(individual);
    }


    public Mono<LoginResponse> login(AuthRequest request) {
        return personService.findUserByUsername(request.getUsername().toLowerCase())
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Username not found")))
                .flatMap(user -> keycloakService.login(request));
    }
}
