package net.proselyte.individualsapi.service;

import lombok.RequiredArgsConstructor;
import net.proselyte.individualsapi.common.dto.IndividualDto;
import net.proselyte.individualsapi.entity.IndividualEntity;
import net.proselyte.individualsapi.entity.UserEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final IndividualService individualService;
    private final UserService userService;

    public Mono<IndividualEntity> createIndividual(IndividualDto individual) {
        return individualService.create(individual);
    }

    public Mono<IndividualEntity> getMyIndividual(String username) {
        return individualService.findByUsername(username);
    }

    public Mono<IndividualEntity> updateIndividual(IndividualDto individual) {
        return individualService.update(individual);
    }

    public Mono<UserEntity> findUserByUsername(String username) {
        return userService.findByUsername(username);
    }
}
