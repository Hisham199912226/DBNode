package com.example.dbnode.api.client.controller.document;

import com.example.dbnode.api.client.service.PathValidationService;
import com.example.dbnode.api.client.service.document.UpdateDocumentService;
import com.example.dbnode.api.client.model.UpdateOneRequestBody;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UpdateDocumentController {
    private final UpdateDocumentService updateService;
    private final PathValidationService pathValidationService;

    @PutMapping("node/client/update/document/one/{databaseName}/{collectionName}")
    public ResponseEntity<String> updateOneDocument(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody UpdateOneRequestBody updateOneRequestBody) throws IOException {
        if(databaseName.equals("db_system"))
            return preventUpdateInSystemDatabase();

        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;
        String jsonObject = updateOneRequestBody.getJsonObject().toString();
        String newContent = updateOneRequestBody.getNewContent().toString();
        boolean isDocumentUpdated = updateService.updateOneDocument(databaseName,collectionName,jsonObject,newContent);
        if(isDocumentUpdated)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Document was successfully updated!");

        return ResponseEntityCreator.getResponse(HttpStatus.BAD_REQUEST,"Failed to update! ensure that collection has documents " +
                "and you did not violate the collection schema");
    }



    @PutMapping("node/client/update/document/{databaseName}/{collectionName}/{id}")
    public ResponseEntity<String> updateDocumentByID(@PathVariable String databaseName, @PathVariable String collectionName, @PathVariable String id, @RequestBody String newContent) throws IOException {
        if(databaseName.equals("db_system"))
            return preventUpdateInSystemDatabase();

        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;

        boolean isDocumentUpdated = updateService.updateDocumentByID(databaseName,collectionName,id,newContent);
        if(isDocumentUpdated)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Document was successfully updated!");

        return ResponseEntityCreator.getResponse(HttpStatus.BAD_REQUEST,"Failed to update! ensure that you provide an existing document id " +
                "and you did not violate the collection schema");
    }

    private ResponseEntity<String> preventUpdateInSystemDatabase() {
        return ResponseEntityCreator.getResponse(HttpStatus.FORBIDDEN,"you cannot update info in db_system");
    }


}
