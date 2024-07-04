package net.proselyte.individualsapi.repository;

import net.proselyte.individualsapi.entity.IndividualEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface IndividualRepository extends R2dbcRepository<IndividualEntity, String> {
}
