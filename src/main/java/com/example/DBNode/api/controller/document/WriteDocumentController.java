package com.example.DBNode.api.controller.document;

import com.example.DBNode.api.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class WriteDocumentController {

    private final DocumentService documentService;
    @PostMapping("node/add/document/{databaseName}/{collectionName}")
    public ResponseEntity<String> addDocument(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject) throws IOException {
        if(!checkIfDatabaseExist(databaseName))
            return getResponse(HttpStatus.NOT_FOUND,"Database you provided does not exist!");
        if(!checkIfCollectionExist(databaseName,collectionName))
            return getResponse(HttpStatus.NOT_FOUND,"Collection you provided does not exist!");
        boolean isDocumentCreated = documentService.addDocument(databaseName,collectionName,jsonObject);
        if(isDocumentCreated)
            return getResponse(HttpStatus.CREATED,"Document was successfully created!");
        return getResponse(HttpStatus.NOT_ACCEPTABLE,"Document failed to pass schema validation!\n" + documentService.readCollectionSchema(databaseName,collectionName));
    }

    private ResponseEntity<String> getResponse(HttpStatus status, String body){
        if(status == null || body == null)
            throw new IllegalArgumentException();
        return ResponseEntity.status(status).body(body);
    }

    private boolean checkIfDatabaseExist(String databaseName){
        return documentService.checkIfDatabaseExist(databaseName);
    }

    private boolean checkIfCollectionExist(String databaseName, String collectionName){
        return documentService.checkIfCollectionExist(databaseName,collectionName);
    }




}
