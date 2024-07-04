package net.proselyte.individualsapi.rest;

import lombok.RequiredArgsConstructor;
import net.proselyte.individualsapi.dto.IndividualDto;
import net.proselyte.individualsapi.service.PersonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/persons")
@RequiredArgsConstructor
public class PersonRestControllerV1 {

    private final PersonService personService;

    @GetMapping("/individuals/info")
    public Mono<IndividualDto> getMyIndividual() {
        return personService.getMyIndividual()
                .map(IndividualDto::fromJpaEntity);
    }
}
