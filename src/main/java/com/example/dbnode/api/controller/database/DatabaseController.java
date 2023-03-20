package com.example.dbnode.api.controller.database;

import com.example.dbnode.api.service.database.DatabaseService;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("node/delete/database/{databaseName}")
    public ResponseEntity<String> deleteDatabase(@PathVariable("databaseName") String databaseName){
        boolean isDatabaseDeleted = databaseService.deleteDatabase(databaseName);
        if(isDatabaseDeleted)
            return getResponse(HttpStatus.OK,"Database was deleted successfully!");
        else
            return getResponse(HttpStatus.NOT_FOUND,"Database you tried to delete does not exist");
    }

    @GetMapping("node/list/databases")
    public ResponseEntity<String> listCollections(){
        return ResponseEntityCreator.getResponse(HttpStatus.OK,databaseService.listDatabases());
    }

    private ResponseEntity<String> getResponse(HttpStatus status, String body){
        if(status == null || body == null)
            throw new IllegalArgumentException();
        return ResponseEntity.status(status).body(body);
    }
}
