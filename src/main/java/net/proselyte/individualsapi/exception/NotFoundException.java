package net.proselyte.individualsapi.exception;

public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(message, "NOT_FOUND");
    }
}
