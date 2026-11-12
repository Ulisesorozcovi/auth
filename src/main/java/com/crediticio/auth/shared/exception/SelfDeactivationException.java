package com.crediticio.auth.shared.exception;

public class SelfDeactivationException extends RuntimeException {

    public SelfDeactivationException(String message) {
        super(message);
    }
}
