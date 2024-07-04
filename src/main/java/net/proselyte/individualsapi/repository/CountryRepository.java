package net.proselyte.individualsapi.repository;

import net.proselyte.individualsapi.entity.CountryEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CountryRepository extends R2dbcRepository<CountryEntity, Integer> {

    Mono<CountryEntity> findByName(String name);
}
