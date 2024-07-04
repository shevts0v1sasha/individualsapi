package net.proselyte.individualsapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import net.proselyte.individualsapi.entity.AddressEntity;
import net.proselyte.individualsapi.entity.IndividualEntity;
import net.proselyte.individualsapi.entity.UserEntity;

@Data
@Builder(toBuilder = true)
public class IndividualDto {

    private String id;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private AddressDto address;
    private String passportNumber;
    private String phoneNumber;

    public static IndividualDto fromJpaEntity(IndividualEntity individualEntity) {
        UserEntity userEntity = individualEntity.getUser();
        AddressEntity addressEntity = userEntity.getAddressEntity();
        return IndividualDto.builder()
                .id(individualEntity.getId().toString())
                .username(userEntity.getUsername())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(individualEntity.getEmail())
                .address(
                        new AddressDto(addressEntity.getCountry().getName(), addressEntity.getAddress(),
                                addressEntity.getState(), addressEntity.getCity(), addressEntity.getZipCode())
                )
                .passportNumber(individualEntity.getPassportNumber())
                .phoneNumber(individualEntity.getPhoneNumber())
                .build();
    }
}
