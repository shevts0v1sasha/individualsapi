package net.proselyte.individualsapi.repository;

import io.r2dbc.postgresql.codec.Json;
import net.proselyte.individualsapi.entity.ProfileHistoryEntity;
import net.proselyte.individualsapi.entity.ProfileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
public class ProfileHistoryRepositoryTests {

    @Autowired
    private ProfileHistoryRepository profileHistoryRepository;

    @BeforeEach
    public void setUp() {
        profileHistoryRepository.deleteAll().block();
    }

    @Test
    @DisplayName("Save profile history functionality")
    public void givenProfileHistoryTransient_whenSave_thenObjectSuccessfullySaved() {
        //given

        String jsonStr = """
                {
                    "first_name": "Alexandr",
                    "last_name": "Shevtsov"
                }
                """;
        Json json = Json.of(jsonStr);

        ProfileHistoryEntity entity = ProfileHistoryEntity.builder()
                .created(LocalDateTime.now())
                .profileType(ProfileType.INDIVIDUAL)
                .reason("No reason")
                .comment("No comments")
                .changedValues(json)
                .build();

        //when
        profileHistoryRepository.save(entity).block();

        //then
        assertThat(entity.getId()).isNotNull();
    }

}
