package net.proselyte.individualsapi.repository;

import net.proselyte.individualsapi.entity.AddressEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface AddressRepository extends R2dbcRepository<AddressEntity, UUID> {
}
