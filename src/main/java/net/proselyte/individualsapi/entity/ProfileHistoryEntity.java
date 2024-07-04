package net.proselyte.individualsapi.entity;

import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "profile_history", schema = "person")
@Builder
public class ProfileHistoryEntity {

    @Id
    private UUID id;

    @Column("created")
    private LocalDateTime created;

    @Column("profile_id")
    private UUID profileId;

    @Transient
    private UserEntity profile;

    @Column("profile_type")
    private ProfileType profileType;

    @Column("reason")
    private String reason;

    @Column("comment")
    private String comment;

    @Column("changed_values")
    private Json changedValues;
}
