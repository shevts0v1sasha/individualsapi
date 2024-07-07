package net.proselyte.individualsapi.repository;

import net.proselyte.individualsapi.entity.IndividualEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IndividualRepository extends R2dbcRepository<IndividualEntity, UUID> {

    Mono<IndividualEntity> findByUserId(UUID id);
}
