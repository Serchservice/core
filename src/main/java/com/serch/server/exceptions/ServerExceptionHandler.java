package com.serch.server.exceptions;

import com.serch.server.bases.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ServerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(HelpException.class)
    public ApiResponse<String> handleSerchException(HelpException exception) {
        ApiResponse<String> response = new ApiResponse<>(exception.getMessage());
        response.setData(exception.getLocalizedMessage());
        return response;
    }
}