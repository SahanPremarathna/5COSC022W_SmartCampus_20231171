package com.mycompany.cosc022w_smartcampus_20231171.exception;

/**
 * Thrown when a sensor cannot accept readings due to its current state.
 */
public class SensorUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message exception message
     */
    public SensorUnavailableException(String message) {
        super(message);
    }
}
