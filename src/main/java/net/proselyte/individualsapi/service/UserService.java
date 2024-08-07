package net.proselyte.individualsapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.proselyte.individualsapi.entity.AddressEntity;
import net.proselyte.individualsapi.entity.EntityStatus;
import net.proselyte.individualsapi.entity.UserEntity;
import net.proselyte.individualsapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AddressService addressService;

    public Mono<UserEntity> create(String username, String firstName, String lastName, String email, AddressEntity addressEntity) {
        LocalDateTime now = LocalDateTime.now();
        UserEntity newUser = UserEntity.builder()
                .secretKey(UUID.randomUUID().toString())
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

        return userRepository.save(newUser);
    }

    public Mono<UserEntity> findByUsername(String username) {
        return completeUserEntityWithAddressEntity(userRepository.findByUsername(username));
    }

    public Mono<UserEntity> update(UserEntity user) {
        return addressService.update(user.getAddressEntity())
                .flatMap(addressEntity -> userRepository.save(user));
    }

    public Mono<UserEntity> findById(UUID userId) {
        return completeUserEntityWithAddressEntity(userRepository.findById(userId));
    }

    private Mono<UserEntity> completeUserEntityWithAddressEntity(Mono<UserEntity> userEntityMono) {
        return userEntityMono
                .flatMap(userEntity -> Mono.zip(Mono.just(userEntity),
                                addressService.findById(userEntity.getAddressId()))
                        .map(objects -> {
                            objects.getT1().setAddressEntity(objects.getT2());
                            return objects.getT1();
                        }));
    }
}
