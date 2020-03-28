package com.cds.mini.error;

public class ServiceException extends RuntimeException {

    private final Errors error;

    public ServiceException(Errors error) {
        this(error, (Throwable) null);
    }

    public ServiceException(Errors error, Throwable cause) {
        this(error, null, cause);
    }

    public ServiceException(Errors error, String additionalMessage, Throwable cause) {
        super(error.getMessage() + (additionalMessage != null && additionalMessage.length() > 0 ? " - " + additionalMessage : ""), cause);
        this.error = error;
    }

    public Errors getError() {
        return error;
    }
}
