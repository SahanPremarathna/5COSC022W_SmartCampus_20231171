package com.mycompany.cosc022w_smartcampus_20231171.model;

import java.time.Instant;

/**
 * Standard JSON error body returned by exception mappers and safe handlers.
 */
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;
    private String timestamp;

    /**
     * Creates an empty error response for JSON serialization frameworks.
     */
    public ErrorResponse() {
    }

    /**
     * Creates a fully populated error response.
     *
     * @param status HTTP status code
     * @param error HTTP reason phrase
     * @param message error details safe for clients
     * @param path request path
     * @param timestamp ISO-8601 timestamp
     */
    public ErrorResponse(int status, String error, String message, String path, String timestamp) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }

    /**
     * Factory helper that fills timestamp automatically.
     *
     * @param status HTTP status code
     * @param error HTTP reason phrase
     * @param message safe message for clients
     * @param path request path
     * @return populated error response
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, Instant.now().toString());
    }

    /**
     * @return HTTP status code
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status HTTP status code
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return HTTP reason phrase
     */
    public String getError() {
        return error;
    }

    /**
     * @param error HTTP reason phrase
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * @return error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message error message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return request path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path request path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return ISO-8601 timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp ISO-8601 timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
