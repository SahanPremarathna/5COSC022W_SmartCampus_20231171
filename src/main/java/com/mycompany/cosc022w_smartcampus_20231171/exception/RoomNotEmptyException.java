package com.mycompany.cosc022w_smartcampus_20231171.exception;

/**
 * Thrown when attempting to delete a room that still has sensors assigned.
 */
public class RoomNotEmptyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message exception message
     */
    public RoomNotEmptyException(String message) {
        super(message);
    }
}
