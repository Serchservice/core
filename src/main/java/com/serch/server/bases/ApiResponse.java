package com.serch.server.bases;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Hidden
@ToString
public class ApiResponse<T> {
    private HttpStatus status;
    private Integer code;
    private String message;
    private T data;

    public ApiResponse(String message) {
        this.data = null;
        this.message = message;
        this.status = HttpStatus.BAD_REQUEST;
        this.code = HttpStatus.BAD_REQUEST.value();
    }

    public ApiResponse(String message, T data, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
        this.code = status.value();
    }
}