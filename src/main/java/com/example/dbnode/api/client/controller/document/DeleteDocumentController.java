package com.example.dbnode.api.client.controller.document;

import com.example.dbnode.api.client.service.PathValidationService;
import com.example.dbnode.api.client.service.document.DeleteDocumentService;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class DeleteDocumentController {
    private final DeleteDocumentService deleteService;
    private final PathValidationService pathValidationService;

    @DeleteMapping("node/client/delete/document/one/{databaseName}/{collectionName}")
    public ResponseEntity<String> deleteOneDocument(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject) throws IOException {
        if(databaseName.equals("db_system"))
            return preventDeleteFromSystemDatabase();

        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;

        boolean isDocumentDeleted = deleteService.deleteOneDocument(databaseName,collectionName,jsonObject);
        if(isDocumentDeleted)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Document was successfully deleted!");
        return ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND,"Document not found to delete!\n");
    }

    @DeleteMapping("node/client/delete/document/many/{databaseName}/{collectionName}")
    public ResponseEntity<String> deleteManyDocuments(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject) throws IOException {
        if(databaseName.equals("db_system"))
            return preventDeleteFromSystemDatabase();

        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;
        boolean isDocumentDeleted = deleteService.deleteManyDocuments(databaseName,collectionName,jsonObject);
        if(isDocumentDeleted)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Documents were successfully deleted!");
        return ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND,"Documents not found to delete!\n");
    }

    @DeleteMapping("node/client/delete/document/one/{databaseName}/{collectionName}/{id}")
    public ResponseEntity<String> deleteDocumentById(@PathVariable String databaseName, @PathVariable String collectionName, @PathVariable String id) throws IOException {
        if(databaseName.equals("db_system"))
            return preventDeleteFromSystemDatabase();

        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;
        boolean isDocumentDeleted = deleteService.deleteDocumentByID(databaseName,collectionName,id);
        if(isDocumentDeleted)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Document was successfully deleted!");
        return ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND,"Document not found to delete!\n");
    }

    private ResponseEntity<String> preventDeleteFromSystemDatabase(){
        return ResponseEntityCreator.getResponse(HttpStatus.FORBIDDEN,"you cannot delete info from db_system");
    }

}
