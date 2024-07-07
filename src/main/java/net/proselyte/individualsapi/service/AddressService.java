package net.proselyte.individualsapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.proselyte.individualsapi.entity.AddressEntity;
import net.proselyte.individualsapi.entity.CountryEntity;
import net.proselyte.individualsapi.entity.UserEntity;
import net.proselyte.individualsapi.repository.AddressRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;
    private final CountryService countryService;

    public Mono<AddressEntity> create(CountryEntity country, String address, String city, String state, String zipCode) {
        LocalDateTime now = LocalDateTime.now();
        AddressEntity newAddress = AddressEntity.builder()
                .created(now)
                .updated(now)
                .country(country)
                .countryId(country.getId())
                .address(address)
                .zipCode(zipCode)
                .city(city)
                .state(state)
                .build();

        return addressRepository.save(newAddress);
    }

    public Mono<AddressEntity> findById(UUID addressId) {
        return addressRepository.findById(addressId)
                .flatMap(addressEntity -> Mono.zip(
                        Mono.just(addressEntity),
                        countryService.findCountryById(addressEntity.getCountryId())
                ))
                .map(objects -> {
                    objects.getT1().setCountry(objects.getT2());
                    return objects.getT1();
                });
    }

    public Mono<AddressEntity> update(AddressEntity addressEntity) {
        return addressRepository.save(addressEntity);
    }
}
