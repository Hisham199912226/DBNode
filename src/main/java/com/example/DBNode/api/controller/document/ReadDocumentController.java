package com.example.DBNode.api.controller.document;

import com.example.DBNode.api.model.Document;
import com.example.DBNode.api.service.PathValidationService;
import com.example.DBNode.api.service.document.ReadDocumentService;
import com.example.DBNode.utils.DocumentMapper;
import com.example.DBNode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ReadDocumentController {
    private final ReadDocumentService readService;
    private final PathValidationService pathValidationService;
    @PostMapping("node/read/document/one/{databaseName}/{collectionName}")
    public ResponseEntity<String> readDocument(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject) throws IOException {
        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;
        Optional<Document> document = readService.readDocument(databaseName,collectionName,jsonObject);
        if(document.isPresent()) {
            String jsonResponseString = DocumentMapper.DocumentToJsonString(document.get());
            return ResponseEntityCreator.getResponse(HttpStatus.FOUND,jsonResponseString);
        }
        return ResponseEntityCreator.getResponse(HttpStatus.FOUND,"Document not found!");
    }

    @PostMapping("node/read/document/many/{databaseName}/{collectionName}")
    public ResponseEntity<String> readDocuments(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject) throws IOException {
        return null;
    }

}
