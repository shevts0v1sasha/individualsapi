package net.proselyte.individualsapi.rest;

import lombok.RequiredArgsConstructor;
import net.proselyte.individualsapi.common.dto.AddressDto;
import net.proselyte.individualsapi.common.dto.AuthRequest;
import net.proselyte.individualsapi.common.dto.IndividualDto;
import net.proselyte.individualsapi.common.dto.LoginResponse;
import net.proselyte.individualsapi.entity.AddressEntity;
import net.proselyte.individualsapi.service.AuthService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestControllerV1 {

    private final AuthService authService;

    @PostMapping("/individuals/register")
    public Mono<IndividualDto> registerIndividual(@RequestBody IndividualDto individual) {
        return authService.registerIndividual(individual)
                .map(individualEntity -> {
                    AddressEntity address = individualEntity.getUser().getAddressEntity();
                    return IndividualDto.builder()
                            .id(individualEntity.getId().toString())
                            .username(individualEntity.getUser().getUsername())
                            .firstName(individualEntity.getUser().getFirstName())
                            .lastName(individualEntity.getUser().getLastName())
                            .email(individualEntity.getEmail())
                            .passportNumber(individualEntity.getPassportNumber())
                            .phoneNumber(individualEntity.getPhoneNumber())
                            .address(new AddressDto(
                                    address.getCountry().getName(),
                                    address.getAddress(),
                                    address.getState(),
                                    address.getCity(),
                                    address.getZipCode()))
                            .build();
                });

    }

    @PostMapping("/individuals/login")
    public Mono<LoginResponse> login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }
}
