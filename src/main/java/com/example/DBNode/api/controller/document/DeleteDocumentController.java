package com.example.DBNode.api.controller.document;

import com.example.DBNode.api.service.document.DeleteDocumentService;
import com.example.DBNode.api.service.PathValidationService;
import com.example.DBNode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class DeleteDocumentController {
    private final DeleteDocumentService deleteService;
    private final PathValidationService pathValidationService;

    @PostMapping("node/delete/document/one/{databaseName}/{collectionName}")
    public ResponseEntity<String> deleteOneDocument(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject) throws IOException {
        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;

        boolean isDocumentDeleted = deleteService.deleteOneDocument(databaseName,collectionName,jsonObject);
        //System.out.println(isDocumentDeleted);
        if(isDocumentDeleted)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Document was successfully deleted!");
        return ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND,"Document not found to delete!\n");
    }

    @PostMapping("node/delete/document/many/{databaseName}/{collectionName}")
    public ResponseEntity<String> deleteManyDocuments(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject) throws IOException {
        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;
        boolean isDocumentDeleted = deleteService.deleteManyDocuments(databaseName,collectionName,jsonObject);
        if(isDocumentDeleted)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Documents were successfully deleted!");
        return ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND,"Documents not found to delete!\n");
    }

    @PostMapping("node/delete/document/one/{databaseName}/{collectionName}/{id}")
    public ResponseEntity<String> deleteDocumentById(@PathVariable String databaseName, @PathVariable String collectionName, @PathVariable String id) throws IOException {
        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;
        boolean isDocumentDeleted = deleteService.deleteDocumentByID(databaseName,collectionName,id);
        if(isDocumentDeleted)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Document were successfully deleted!");
        return ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND,"Document not found to delete!\n");
    }


}
