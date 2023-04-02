package com.example.dbnode.api.client.controller.collection;

import com.example.dbnode.api.client.service.collection.CollectionService;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionService collectionService;
    private static final String DATABASE_NOT_EXIST_MESSAGE = "Database you provided does not exist";

    @PostMapping("node/client/create/collection/{databaseName}/{collectionName}")
    public ResponseEntity<String> createCollection(@PathVariable("databaseName") String databaseName, @PathVariable("collectionName") String collectionName,@RequestBody String jsonSchema, Principal principal) throws IOException {
        if(databaseName.equals("db_system"))
            return ResponseEntityCreator.getResponse(HttpStatus.FORBIDDEN,"You can not add collection to db_system!");
        if(hasRole(principal,"USER"))
            return ResponseEntityCreator.getResponse(HttpStatus.FORBIDDEN,"You can not add collection as a user! you need the admin role to have this privilege ");
        if(checkIfDatabaseNotExist(databaseName))
            return ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND,DATABASE_NOT_EXIST_MESSAGE);
        boolean isCollectionCreated = collectionService.createCollection(databaseName,collectionName,jsonSchema);
        if(isCollectionCreated)
            return ResponseEntityCreator.getResponse(HttpStatus.CREATED,"Collection was created successfully!");
        else
            return ResponseEntityCreator.getResponse(HttpStatus.CONFLICT,"Collection you tried to create is already exist");
    }


    @DeleteMapping("node/client/delete/collection/{databaseName}/{collectionName}")
    public ResponseEntity<String> deleteCollection(@PathVariable("databaseName") String databaseName, @PathVariable("collectionName") String collectionName, Principal principal){
        if(databaseName.equals("db_system"))
            return ResponseEntityCreator.getResponse(HttpStatus.FORBIDDEN,"You can not delete collections from db_system!");
        if(hasRole(principal,"USER"))
            return ResponseEntityCreator.getResponse(HttpStatus.FORBIDDEN,"You can not delete collection as a user! you need the admin role to have this privilege ");
        if(checkIfDatabaseNotExist(databaseName))
            return ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND,DATABASE_NOT_EXIST_MESSAGE);
        boolean isCollectionDeleted = collectionService.deleteCollection(databaseName,collectionName);
        if(isCollectionDeleted)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Collection was deleted successfully!");
        else
            return ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND,"Collection you tried to delete does not exist");
    }

    @GetMapping("node/client/list/collections/{databaseName}")
    public ResponseEntity<String> listCollections(Principal principal, @PathVariable String databaseName){
        if(databaseName.equals("db_system") && hasRole(principal,"USER"))
            return ResponseEntityCreator.getResponse(HttpStatus.UNAUTHORIZED,"You can not see collections from db_system as a user");

        if(checkIfDatabaseNotExist(databaseName))
            return ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND,DATABASE_NOT_EXIST_MESSAGE);
        return ResponseEntityCreator.getResponse(HttpStatus.OK,collectionService.listCollections(databaseName));
    }

    private boolean hasRole(Principal principal, String roleName) {
        if (principal instanceof Authentication) {
            Authentication authentication = (Authentication) principal;
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(roleName));
        }
        return false;
    }

    private boolean checkIfDatabaseNotExist(String databaseName){
        return !collectionService.checkIfDatabaseExist(databaseName);
    }

}
