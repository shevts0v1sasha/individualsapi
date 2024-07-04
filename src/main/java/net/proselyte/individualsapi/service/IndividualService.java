package net.proselyte.individualsapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.proselyte.individualsapi.dto.IndividualDto;
import net.proselyte.individualsapi.entity.CountryEntity;
import net.proselyte.individualsapi.entity.IndividualEntity;
import net.proselyte.individualsapi.exception.CountryNotFoundException;
import net.proselyte.individualsapi.repository.IndividualRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

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
        String username = individualDto.getUsername();
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


        // find country
        // find or create address
        // create user
            // create user in keycloak
        // add new profile history
        // create individual
        // return

    }

    public Mono<IndividualEntity> findById(String id) {
        // find by id in repository
        // find user
        // find address
        // build dto and return
        return Mono.empty();
    }

    public Mono<IndividualEntity> update() {
        // begin transactoin
        // find by id in repository
        // find user
        // find address
        // update individual
        // update user
        // update address
        // add new row in profile history
        // commit transaction
        return Mono.empty();
    }
}
