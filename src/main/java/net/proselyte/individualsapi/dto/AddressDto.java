package net.proselyte.individualsapi.dto;

public record AddressDto(String country,
                         String address,
                         String state,
                         String city,
                         String zipCode) {
}
