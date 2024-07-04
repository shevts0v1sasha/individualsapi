package net.proselyte.individualsapi.repository;

import net.proselyte.individualsapi.entity.ProfileHistoryEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ProfileHistoryRepository extends R2dbcRepository<ProfileHistoryEntity, String> {
}
