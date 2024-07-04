package net.proselyte.individualsapi.exception;

public class CountryNotFoundException extends ApiException {
    public CountryNotFoundException(String message) {
        super(message, "COUNTRY_NOT_FOUND");
    }
}
