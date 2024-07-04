package net.proselyte.individualsapi.service;

import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import net.proselyte.individualsapi.entity.IndividualEntity;
import net.proselyte.individualsapi.entity.ProfileHistoryEntity;
import net.proselyte.individualsapi.entity.ProfileType;
import net.proselyte.individualsapi.repository.ProfileHistoryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfileHistoryService {

    private final ProfileHistoryRepository profileHistoryRepository;

    public Mono<ProfileHistoryEntity> addNewProfileHistory(IndividualEntity individualEntity) {
        String json = """
                {
                    "first_name": "%s",
                    "last_name": "%s"
                }
                """.formatted(individualEntity.getUser().getFirstName(), individualEntity.getUser().getLastName());
        Json changedValues = Json.of(json);
        ProfileHistoryEntity profileHistory = ProfileHistoryEntity.builder()
                .created(LocalDateTime.now())
                .profileId(individualEntity.getUserId())
                .profileType(ProfileType.INDIVIDUAL)
                .reason("on_create")
                .comment("comment")
                .changedValues(changedValues)
                .build();
        return profileHistoryRepository.save(profileHistory);
    }
}
