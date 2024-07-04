package net.proselyte.individualsapi.entity;

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
@Table(value = "users", schema = "person")
@Builder
public class UserEntity {

    @Id
    private UUID id;

    @Column("secret_key")
    private String secretKey;

    @Column("username")
    private String username;

    @Column("created")
    private LocalDateTime created;

    @Column("updated")
    private LocalDateTime updated;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("verified_at")
    private LocalDateTime verifiedAt;

    @Column("archived_at")
    private LocalDateTime archivedAt;

    @Column("status")
    private EntityStatus status;

    @Column("filled")
    private boolean filled;

    @Column("address_id")
    private UUID addressId;

    @Transient
    private AddressEntity addressEntity;

}
