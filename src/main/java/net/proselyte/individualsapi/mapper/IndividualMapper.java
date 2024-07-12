package net.proselyte.individualsapi.mapper;

import net.proselyte.individualsapi.common.dto.AddressDto;
import net.proselyte.individualsapi.common.dto.IndividualDto;
import net.proselyte.individualsapi.entity.AddressEntity;
import net.proselyte.individualsapi.entity.IndividualEntity;
import net.proselyte.individualsapi.entity.UserEntity;

public final class IndividualMapper {

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
