package com.learning.blog.controller;

import com.learning.blog.exception.ResourceNotFoundException;
import com.learning.blog.model.dtos.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class ErrorController {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        ApiErrorResponse error = ApiErrorResponse.builder().
                status(HttpStatus.INTERNAL_SERVER_ERROR.value()).
                message("An unexpected error occurred").
                build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ApiErrorResponse error = ApiErrorResponse.builder().
                status(HttpStatus.BAD_REQUEST.value()).
                message(e.getMessage()).
                build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException e) {
        ApiErrorResponse error = ApiErrorResponse.builder().
                status(HttpStatus.CONFLICT.value()).
                message(e.getMessage()).
                build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        ApiErrorResponse error = ApiErrorResponse.builder().
                status(HttpStatus.UNAUTHORIZED.value()).
                message("Invalid username or password").
                build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        ApiErrorResponse error = ApiErrorResponse.builder().
                status(HttpStatus.NOT_FOUND.value()).
                message(e.getMessage()).
                build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
