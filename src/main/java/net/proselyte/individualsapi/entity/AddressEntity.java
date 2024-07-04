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
@Table(value = "addresses", schema = "person")
@Builder
public class AddressEntity {

    @Id
    private UUID id;

    @Column("created")
    private LocalDateTime created;

    @Column("updated")
    private LocalDateTime updated;

    @Transient
    private CountryEntity country;

    @Column("country_id")
    private Integer countryId;

    @Column("address")
    private String address;

    @Column("zip_code")
    private String zipCode;

    @Column("archived")
    private LocalDateTime archived;

    @Column("city")
    private String city;

    @Column("state")
    private String state;
}
