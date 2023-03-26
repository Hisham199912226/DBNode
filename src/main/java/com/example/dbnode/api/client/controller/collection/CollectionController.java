package com.example.dbnode.api.client.controller.collection;

import com.example.dbnode.api.client.service.collection.CollectionService;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionService collectionService;
    private static final String DATABASE_NOT_EXIST_MESSAGE = "Database you provided does not exist";

    @PostMapping("node/client/create/collection/{databaseName}/{collectionName}")
    public ResponseEntity<String> createCollection(@PathVariable("databaseName") String databaseName, @PathVariable("collectionName") String collectionName){

        if(!checkIfDatabaseExist(databaseName))
            return getResponse(HttpStatus.NOT_FOUND,DATABASE_NOT_EXIST_MESSAGE);
        boolean isCollectionCreated = collectionService.createCollection(databaseName,collectionName);
        if(isCollectionCreated)
            return getResponse(HttpStatus.CREATED,"Collection was created successfully!");
        else
            return getResponse(HttpStatus.CONFLICT,"Collection you tried to create is already exist");
    }


    @DeleteMapping("node/client/delete/collection/{databaseName}/{collectionName}")
    public ResponseEntity<String> deleteCollection(@PathVariable("databaseName") String databaseName, @PathVariable("collectionName") String collectionName){
        if(!checkIfDatabaseExist(databaseName))
            return getResponse(HttpStatus.NOT_FOUND,DATABASE_NOT_EXIST_MESSAGE);
        boolean isCollectionDeleted = collectionService.deleteCollection(databaseName,collectionName);
        if(isCollectionDeleted)
            return getResponse(HttpStatus.OK,"Collection was deleted successfully!");
        else
            return getResponse(HttpStatus.NOT_FOUND,"Collection you tried to delete does not exist");
    }

    @GetMapping("node/client/list/collections/{databaseName}")
    public ResponseEntity<String> listCollections(@PathVariable String databaseName){
        if(!checkIfDatabaseExist(databaseName))
            return getResponse(HttpStatus.NOT_FOUND,DATABASE_NOT_EXIST_MESSAGE);
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
