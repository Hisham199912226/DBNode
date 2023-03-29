package com.example.dbnode.api.redirection.controller;

import com.example.dbnode.api.client.service.document.UpdateDocumentService;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class RedirectUpdateDocumentController {

    private final UpdateDocumentService updateDocumentService;

    @PutMapping("node/redirect/update/document/{databaseName}/{collectionName}")
    public ResponseEntity<String> updateRedirectedDocument(@PathVariable String databaseName, @PathVariable String collectionName, @RequestParam String documentId, @RequestBody String newContent) throws IOException {
        boolean isDocumentUpdated = updateDocumentService.updateDocumentByID(databaseName,collectionName,documentId,newContent);
        if(isDocumentUpdated)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Document was successfully updated!");

        return ResponseEntityCreator.getResponse(HttpStatus.BAD_REQUEST,"Failed to update! ensure that you provide an existing document id " +
                "and you did not violate the collection schema");
    }
}
