package net.proselyte.individualsapi.exception;

public class NotAuthorizedException extends ApiException {
    public NotAuthorizedException(String message) {
        super(message, "NOT_AUTHORIZED");
    }
}
