package net.proselyte.individualsapi.exception;

public class KeycloakUserNotFoundException extends ApiException {
    public KeycloakUserNotFoundException(String message) {
        super(message, "NOT_FOUND");
    }
}
