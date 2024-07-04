package net.proselyte.individualsapi.service;

import lombok.RequiredArgsConstructor;
import net.proselyte.individualsapi.entity.CountryEntity;
import net.proselyte.individualsapi.repository.CountryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public Mono<CountryEntity> findCountryByName(String country) {
        return countryRepository.findByName(country);
    }

    public Mono<CountryEntity> findCountryById(Integer countryId) {
        return countryRepository.findById(countryId);
    }
}
