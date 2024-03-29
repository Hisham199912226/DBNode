package com.example.dbnode.api.broadcast.controller.collection;

import com.example.dbnode.api.broadcast.service.collection.BroadcastCollectionService;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class BroadcastCollectionController {

    private final BroadcastCollectionService broadcastCollectionService;

    @PostMapping("node/broadcast/collection/create/{databaseName}/{collectionName}")
    public ResponseEntity<String> receiveCreateCollectionChange(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonSchema) throws IOException {
        boolean isCollectionCreated = broadcastCollectionService.createCollection(databaseName,collectionName,jsonSchema);
        if(isCollectionCreated)
            return ResponseEntityCreator.getResponse(HttpStatus.CREATED, "collection from broadcast successfully created!");
        return ResponseEntityCreator.getResponse(HttpStatus.BAD_REQUEST, "There is a problem with creating collection received from a broadcast message!");
    }

    @PostMapping("node/broadcast/collection/delete/{databaseName}/{collectionName}")
    public ResponseEntity<String> receiveDeleteCollectionChange(@PathVariable String databaseName, @PathVariable String collectionName){
        boolean isCollectionDeleted = broadcastCollectionService.deleteCollection(databaseName,collectionName);
        if(isCollectionDeleted)
            return ResponseEntityCreator.getResponse(HttpStatus.CREATED, "collection from broadcast successfully created!");
        return ResponseEntityCreator.getResponse(HttpStatus.BAD_REQUEST, "There is a problem with deleting collection received from a broadcast message!");
    }
}
