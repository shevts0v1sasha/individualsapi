package net.proselyte.individualsapi.exception;

public class KeycloakBadRequestException extends ApiException {
    public KeycloakBadRequestException(String message) {
        super(message, "BAD_REQUEST");
    }
}
