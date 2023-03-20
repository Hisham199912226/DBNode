package com.example.dbnode.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityCreator {

    private ResponseEntityCreator(){

    }
   public static ResponseEntity<String> getResponse(HttpStatus status, String body){
        if(status == null || body == null)
            throw new IllegalArgumentException();
        return ResponseEntity.status(status).body(body);
    }
}
