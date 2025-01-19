package com.serch.server.bases;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 * The ApiResponse class represents a generic response model for API endpoints.
 * It encapsulates the HTTP status, response code, message, and optional response payload.
 * This class is typically used to standardize the format of responses returned by API endpoints.
 *
 * @param <T> The type of response payload included in the response.
 */
@Getter
@Setter
@Hidden
@ToString
public class ApiResponse<T> {
    /**
     * The HTTP status of the response.
     */
    private HttpStatus status;

    /**
     * The response code corresponding to the HTTP status.
     */
    private Integer code;

    /**
     * The message associated with the response.
     */
    private String message;

    /**
     * The response payload included in the response.
     */
    private T data;

    /**
     * Constructs an ApiResponse object with a message and sets default values for status and code.
     *
     * @param message The message associated with the response.
     */
    public ApiResponse(String message) {
        this.data = null;
        this.message = message;
        this.status = HttpStatus.BAD_REQUEST;
        this.code = HttpStatus.BAD_REQUEST.value();
    }

    /**
     * Constructs an ApiResponse object with response and sets default values for message, status, and code.
     *
     * @param data The response payload included in the response.
     */
    public ApiResponse(T data) {
        this.data = data;
        this.message = "Successful";
        this.status = HttpStatus.OK;
        this.code = HttpStatus.OK.value();
    }

    /**
     * Constructs an ApiResponse object with a message, status, and sets code based on the status.
     *
     * @param message The message associated with the response.
     * @param status  The HTTP status of the response.
     */
    public ApiResponse(String message, HttpStatus status) {
        this.data = null;
        this.message = message;
        this.status = status;
        this.code = status.value();
    }

    /**
     * Constructs an ApiResponse object with a message, response, status, and sets code based on the status.
     *
     * @param message The message associated with the response.
     * @param data    The response payload included in the response.
     * @param status  The HTTP status of the response.
     */
    public ApiResponse(String message, T data, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
        this.code = status.value();
    }
}