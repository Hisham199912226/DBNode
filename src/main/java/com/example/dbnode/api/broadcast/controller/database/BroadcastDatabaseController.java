package com.example.dbnode.api.broadcast.controller.database;

import com.example.dbnode.api.broadcast.service.database.BroadcastDatabaseService;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class BroadcastDatabaseController {
    private final BroadcastDatabaseService broadcastDatabaseService;

    @PostMapping("node/broadcast/database/create/{databaseName}")
    public ResponseEntity<String> receiveCreateDatabaseChange(@PathVariable String databaseName){
        boolean isDatabaseCreated = broadcastDatabaseService.createDatabase(databaseName);
        if(isDatabaseCreated)
            return ResponseEntityCreator.getResponse(HttpStatus.CREATED, "database from broadcast successfully created!");
        return ResponseEntityCreator.getResponse(HttpStatus.BAD_REQUEST, "There is a problem with creating database received from a broadcast message!");
    }

    @PostMapping("node/broadcast/database/delete/{databaseName}")
    public ResponseEntity<String> receiveDeleteDatabaseChange(@PathVariable String databaseName){
        boolean isDatabaseDeleted = broadcastDatabaseService.deleteDatabase(databaseName);
        if(isDatabaseDeleted)
            return ResponseEntityCreator.getResponse(HttpStatus.CREATED, "database from broadcast successfully created!");
        return ResponseEntityCreator.getResponse(HttpStatus.BAD_REQUEST, "There is a problem with deleting database received from a broadcast message!");
    }
}
