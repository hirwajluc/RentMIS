package com.rentmis.exception;

import org.springframework.http.HttpStatus;

public class RentMISException extends RuntimeException {
    private final HttpStatus status;

    public RentMISException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static RentMISException notFound(String message) {
        return new RentMISException(message, HttpStatus.NOT_FOUND);
    }

    public static RentMISException badRequest(String message) {
        return new RentMISException(message, HttpStatus.BAD_REQUEST);
    }

    public static RentMISException forbidden(String message) {
        return new RentMISException(message, HttpStatus.FORBIDDEN);
    }

    public static RentMISException conflict(String message) {
        return new RentMISException(message, HttpStatus.CONFLICT);
    }

    public static RentMISException unauthorized(String message) {
        return new RentMISException(message, HttpStatus.UNAUTHORIZED);
    }
}
