package com.example.dbnode.api.controller.document;

import com.example.dbnode.api.service.document.AddDocumentService;
import com.example.dbnode.api.service.document.ReadDocumentService;
import com.example.dbnode.api.service.PathValidationService;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class AddDocumentController {

    private final AddDocumentService addService;
    private final ReadDocumentService readService;
    private final PathValidationService pathValidationService;

    @PostMapping("node/add/document/{databaseName}/{collectionName}")
    public ResponseEntity<String> addDocument(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject) throws IOException {
        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;

        boolean isDocumentCreated = addService.addDocument(databaseName,collectionName,jsonObject);
        if(isDocumentCreated)
            return ResponseEntityCreator.getResponse(HttpStatus.CREATED,"Document was successfully created!");
        String collectionSchema = readService.readCollectionSchema(databaseName,collectionName);
        return ResponseEntityCreator.getResponse(HttpStatus.NOT_ACCEPTABLE,"Document failed to pass schema validation!\n".concat(collectionSchema));
    }
}
