package net.proselyte.individualsapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "countries", schema = "person")
public class CountryEntity {

    @Id
    private Integer id;

    @Column("created")
    private LocalDateTime created;

    @Column("updated")
    private LocalDateTime updated;

    @Column("name")
    private String name;

    @Column("alpha2")
    private String alpha2;

    @Column("alpha3")
    private String alpha3;
}
