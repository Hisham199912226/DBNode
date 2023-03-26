package com.example.dbnode;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BadCredentialsExceptionHandler {

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<String> badCredentialsExceptionHandler(BadCredentialsException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }
}
