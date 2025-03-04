package com.cunhaedu.admin.catalogo.domain.exceptions;

public class NoStackTraceException extends RuntimeException {

    NoStackTraceException(final String message) {
        this(message, null);
    }

    NoStackTraceException(final String message, Throwable cause) {
        super(message, cause, true, false);
    }
}
