package com.example.DBNode.api.controller;

import com.example.DBNode.api.service.CollectionService;
import com.example.DBNode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionService collectionService;
    @PostMapping("node/create/collection/{databaseName}/{collectionName}")
    public ResponseEntity<String> createCollection(@PathVariable("databaseName") String databaseName, @PathVariable("collectionName") String collectionName){
        if(!checkIfDatabaseExist(databaseName))
            return getResponse(HttpStatus.NOT_FOUND,"Database you provided does not exist");
        boolean isCollectionCreated = collectionService.createCollection(databaseName,collectionName);
        if(isCollectionCreated)
            return getResponse(HttpStatus.CREATED,"Collection was created successfully!");
        else
            return getResponse(HttpStatus.CONFLICT,"Collection you tried to create is already exist");
    }

    @PostMapping("node/delete/collection/{databaseName}/{collectionName}")
    public ResponseEntity<String> deleteCollection(@PathVariable("databaseName") String databaseName, @PathVariable("collectionName") String collectionName){
        if(!checkIfDatabaseExist(databaseName))
            return getResponse(HttpStatus.NOT_FOUND,"Database you provided does not exist");
        boolean isCollectionDeleted = collectionService.deleteCollection(databaseName,collectionName);
        if(isCollectionDeleted)
            return getResponse(HttpStatus.OK,"Collection was deleted successfully!");
        else
            return getResponse(HttpStatus.NOT_FOUND,"Collection you tried to delete does not exist");
    }

    @GetMapping("node/list/collections/{databaseName}")
    public ResponseEntity<String> listCollections(@PathVariable String databaseName){
        if(!checkIfDatabaseExist(databaseName))
            return getResponse(HttpStatus.NOT_FOUND,"Database you provided does not exist");
        return ResponseEntityCreator.getResponse(HttpStatus.OK,collectionService.listCollections(databaseName));
    }

    private boolean checkIfDatabaseExist(String databaseName){
        return collectionService.checkIfDatabaseExist(databaseName);
    }

    private ResponseEntity<String> getResponse(HttpStatus status, String body){
        if(status == null || body == null)
            throw new IllegalArgumentException();
        return ResponseEntity.status(status).body(body);
    }
}
