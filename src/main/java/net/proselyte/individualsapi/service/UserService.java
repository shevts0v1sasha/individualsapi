package net.proselyte.individualsapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.proselyte.individualsapi.entity.AddressEntity;
import net.proselyte.individualsapi.entity.EntityStatus;
import net.proselyte.individualsapi.entity.UserEntity;
import net.proselyte.individualsapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final KeycloakService keycloakService;
    private final UserRepository userRepository;
    private final AddressService addressService;

    public Mono<UserEntity> create(String username, String password, String firstName, String lastName, String email, AddressEntity addressEntity) {
        LocalDateTime now = LocalDateTime.now();
        UserEntity newUser = UserEntity.builder()
                .secretKey("secret")
                .username(username)
                .created(now)
                .updated(now)
                .firstName(firstName)
                .lastName(lastName)
                .status(EntityStatus.NOT_VERIFIED)
                .filled(false)
                .addressEntity(addressEntity)
                .addressId(addressEntity.getId())
                .build();

        return userRepository.save(newUser)
                .flatMap(userEntity -> Mono.zip(Mono.just(userEntity), keycloakService.register(username, password, firstName, lastName, email)))
                .map(Tuple2::getT1);
    }

    public Mono<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .flatMap(userEntity -> Mono.zip(Mono.just(userEntity),
                                addressService.findById(userEntity.getAddressId()))
                        .map(objects -> {
                            objects.getT1().setAddressEntity(objects.getT2());
                            return objects.getT1();
                        }));
    }
}
