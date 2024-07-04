package net.proselyte.individualsapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "individuals", schema = "person")
@Builder
public class IndividualEntity {

    @Id
    private UUID id;

    @Transient
    private UserEntity user;

    @Column("user_id")
    private UUID userId;

    @Column("passport_number")
    private String passportNumber;

    @Column("phone_number")
    private String phoneNumber;

    @Column("email")
    private String email;
}
