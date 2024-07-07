package net.proselyte.individualsapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.proselyte.individualsapi.dto.AddressDto;
import net.proselyte.individualsapi.dto.IndividualDto;
import net.proselyte.individualsapi.entity.AddressEntity;
import net.proselyte.individualsapi.entity.CountryEntity;
import net.proselyte.individualsapi.entity.IndividualEntity;
import net.proselyte.individualsapi.entity.UserEntity;
import net.proselyte.individualsapi.exception.CountryNotFoundException;
import net.proselyte.individualsapi.repository.IndividualRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndividualService {

    private final UserService userService;
    private final AddressService addressService;
    private final ProfileHistoryService profileHistoryService;
    private final CountryService countryService;
    private final IndividualRepository individualRepository;

    @Transactional
    public Mono<IndividualEntity> create(IndividualDto individualDto) {
        String address = individualDto.getAddress().address();
        String city = individualDto.getAddress().city();
        String state = individualDto.getAddress().state();
        String zipCode = individualDto.getAddress().zipCode();
        String firstName = individualDto.getFirstName();
        String lastName = individualDto.getLastName();
        String password = individualDto.getPassword();
        String username = individualDto.getUsername().toLowerCase(); // because keycloak doesn't support upper case letters
        String email = individualDto.getEmail();

        Mono<CountryEntity> country = countryService.findCountryByName(individualDto.getAddress().country());
        return country
                .switchIfEmpty(Mono.error(new CountryNotFoundException("Couldn't find country with name: %s".formatted(individualDto.getAddress().country()))))
                .flatMap(countryEntity -> addressService.create(countryEntity, address, city, state, zipCode)
                        .flatMap(addressEntity -> userService.create(username, password, firstName, lastName, email, addressEntity)
                                .flatMap(userEntity -> {
                                    IndividualEntity individual = IndividualEntity.builder()
                                            .userId(userEntity.getId())
                                            .user(userEntity)
                                            .passportNumber(individualDto.getPassportNumber())
                                            .phoneNumber(individualDto.getPhoneNumber())
                                            .email(email)
                                            .build();
                                    return individualRepository.save(individual)
                                            .flatMap(individualEntity -> Mono.zip(Mono.just(individualEntity), profileHistoryService.addNewProfileHistory(individualEntity)))
                                            .flatMap(zip -> Mono.just(zip.getT1()));
                                })));
    }

    @Transactional
    public Mono<IndividualEntity> update(IndividualDto individualDto) {
        LocalDateTime updateTime = LocalDateTime.now();
        return findById(UUID.fromString(individualDto.getId()))
                .flatMap(individualEntity -> {
                    UserEntity user = individualEntity.getUser();
                    user.setFirstName(individualDto.getFirstName());
                    user.setLastName(individualDto.getLastName());
                    user.setUpdated(updateTime);
                    individualEntity.setPhoneNumber(individualDto.getPhoneNumber());
                    individualEntity.setPassportNumber(individualDto.getPassportNumber());
                    AddressDto addressDto = individualDto.getAddress();
                    AddressEntity address = user.getAddressEntity();
                    address.setAddress(addressDto.address());
                    address.setUpdated(updateTime);
                    address.setZipCode(addressDto.zipCode());
                    address.setCity(addressDto.city());
                    address.setState(addressDto.state());

                    return userService.update(user)
                            .flatMap(updatedUser -> individualRepository.save(individualEntity));
                });
    }

    public Mono<IndividualEntity> findByUsername(String username) {
        return userService.findByUsername(username)
                .flatMap(userEntity -> Mono.zip(Mono.just(userEntity), individualRepository.findByUserId(userEntity.getId())))
                .map(objects -> {
                    objects.getT2().setUser(objects.getT1());
                    return objects.getT2();
                });
    }

    public Mono<IndividualEntity> findById(UUID id) {
        return completeIndividualWithUserEntity(individualRepository.findById(id));
    }

    private Mono<IndividualEntity> completeIndividualWithUserEntity(Mono<IndividualEntity> individualEntityMono) {
        return individualEntityMono
                .flatMap(individualEntity -> Mono.zip(Mono.just(individualEntity), userService.findById(individualEntity.getUserId())))
                .map(objects -> {
                    objects.getT1().setUser(objects.getT2());
                    return objects.getT1();
                });
    }
}
