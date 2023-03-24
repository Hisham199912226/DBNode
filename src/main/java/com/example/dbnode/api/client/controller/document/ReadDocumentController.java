package com.example.dbnode.api.client.controller.document;

import com.example.dbnode.api.client.service.PathValidationService;
import com.example.dbnode.api.client.service.document.ReadDocumentService;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ReadDocumentController {
    private final ReadDocumentService readService;
    private final PathValidationService pathValidationService;

    @PostMapping("node/read/document/one/{databaseName}/{collectionName}")
    public ResponseEntity<String> readOneDocument(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject) throws IOException {
        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;
        Optional<String> jsonResponseString = readService.readDocument(databaseName,collectionName,jsonObject);
        return jsonResponseString.map(s -> ResponseEntityCreator.getResponse(HttpStatus.OK, s)).orElseGet(()
                -> ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND, "Document not found!"));
    }

    @PostMapping("node/read/document/many/{databaseName}/{collectionName}")
    public ResponseEntity<String> readManyDocuments(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject) throws IOException {
        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;
        Optional<String> jsonResponseString = readService.readDocuments(databaseName,collectionName,jsonObject);
        return jsonResponseString.map(s -> ResponseEntityCreator.getResponse(HttpStatus.OK, s)).orElseGet(()
                -> ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND, "Documents not found!"));
    }

    @GetMapping("node/read/document/count/{databaseName}/{collectionName}")
    public ResponseEntity<String> countDocuments(@PathVariable String databaseName, @PathVariable String collectionName){
        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;
        String documentsCount = readService.countDocuments(databaseName,collectionName);
        return ResponseEntityCreator.getResponse(HttpStatus.OK,documentsCount);
    }


}
