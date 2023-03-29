package com.example.dbnode;

import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class JsonParseExceptionHandler {

    @ExceptionHandler(value = JsonParseException.class)
    public ResponseEntity<String> badCredentialsExceptionHandler(JsonParseException e){
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body("There is a syntax error in the provided json!");
    }
}
