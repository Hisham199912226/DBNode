package com.example.DBNode.api.controller;

import com.example.DBNode.api.model.Database;
import com.example.DBNode.api.service.DatabaseService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DatabaseController {
    private final DatabaseService databaseService;
    @PostMapping("node/create/database/{databaseName}")
    public ResponseEntity<String> createDatabase(@PathVariable("databaseName") String databaseName){
        boolean isDatabaseCreated = databaseService.createDatabase(databaseName);
        if(isDatabaseCreated)
            return getResponse(HttpStatus.CREATED,"Database was created successfully!");
        else
            return getResponse(HttpStatus.CONFLICT,"Database you tried to create is already exist");
    }

    @PostMapping("node/delete/database/{databaseName}")
    public ResponseEntity<String> deleteDatabase(@PathVariable("databaseName") String databaseName){
        boolean isDatabaseDeleted = databaseService.deleteDatabase(databaseName);
        if(isDatabaseDeleted)
            return getResponse(HttpStatus.OK,"Database was deleted successfully!");
        else
            return getResponse(HttpStatus.NOT_FOUND,"Database you tried to delete does not exist");
    }

    private ResponseEntity<String> getResponse(HttpStatus status, String body){
        if(status == null || body == null)
            throw new IllegalArgumentException();
        return ResponseEntity.status(status).body(body);
    }
}
