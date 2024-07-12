package net.proselyte.individualsapi.rest;

import lombok.RequiredArgsConstructor;
import net.proselyte.individualsapi.common.dto.IndividualDto;
import net.proselyte.individualsapi.mapper.IndividualMapper;
import net.proselyte.individualsapi.service.PersonService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
public class PersonRestControllerV1 {

    private final PersonService personService;

    @GetMapping("/individuals/info")
    public Mono<IndividualDto> getMyIndividual(Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        return personService.getMyIndividual(jwtAuthenticationToken.getName())
                .map(IndividualMapper::fromJpaEntity);
    }

    @PatchMapping("/individuals")
    public Mono<IndividualDto> updateIndividual(@RequestBody IndividualDto individual) {
        return personService.updateIndividual(individual)
                .map(IndividualMapper::fromJpaEntity);
    }
}
