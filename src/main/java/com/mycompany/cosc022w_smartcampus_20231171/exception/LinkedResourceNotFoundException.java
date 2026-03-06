package com.mycompany.cosc022w_smartcampus_20231171.exception;

/**
 * Thrown when a request references another resource that does not exist.
 */
public class LinkedResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message exception message
     */
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}
