package net.proselyte.individualsapi.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateUserRequest(String username,
                                String password,
                                String firstName,
                                String lastName,
                                String email) {
}
