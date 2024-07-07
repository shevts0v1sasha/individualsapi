package net.proselyte.individualsapi.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class KeycloakUserDto {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
