package com.example.dbnode.api.redirection.controller;

import com.example.dbnode.api.client.service.document.DeleteDocumentService;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class RedirectDeleteDocumentController {

    private final DeleteDocumentService deleteService;
    @DeleteMapping("node/redirect/delete/document/{databaseName}/{collectionName}")
    public ResponseEntity<String> deleteRedirectedDocument(@PathVariable String databaseName, @PathVariable String collectionName, @RequestParam String documentId) throws IOException {
        boolean isDocumentDeleted = deleteService.deleteDocumentByID(databaseName,collectionName,documentId);
        System.out.println("document with id : " + documentId + " redirected to me (deleteRedirectedDocument)");
        if(isDocumentDeleted)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Document was successfully deleted!");
        return ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND,"Document not found to delete!\n");
    }
}
