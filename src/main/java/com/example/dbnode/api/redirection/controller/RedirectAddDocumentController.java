package com.example.dbnode.api.redirection.controller;

import com.example.dbnode.api.client.service.document.*;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RedirectAddDocumentController {

    private final AddDocumentService addService;
    private final ReadDocumentService readService;

    @PostMapping("node/redirect/add/document/{databaseName}/{collectionName}")
    public ResponseEntity<String> addRedirectedDocument(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject) throws IOException {
        log.info("Receive Add Document Redirection");
        boolean isDocumentCreated = addService.generateIdAndAddDocument(databaseName,collectionName,jsonObject);
        if(isDocumentCreated)
            return ResponseEntityCreator.getResponse(HttpStatus.CREATED,"Document was successfully created!");
        String collectionSchema = readService.readCollectionSchema(databaseName,collectionName);
        return ResponseEntityCreator.getResponse(HttpStatus.NOT_ACCEPTABLE,"Document failed to pass schema validation!\n".concat(collectionSchema));
    }
}
