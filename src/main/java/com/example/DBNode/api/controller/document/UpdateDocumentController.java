package com.example.DBNode.api.controller.document;

import com.example.DBNode.api.model.UpdateOneRequestBody;
import com.example.DBNode.api.service.PathValidationService;
import com.example.DBNode.api.service.document.*;
import com.example.DBNode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UpdateDocumentController {
    private final UpdateDocumentService updateService;
    private final PathValidationService pathValidationService;

    @PutMapping("node/update/document/one/{databaseName}/{collectionName}")
    public ResponseEntity<String> updateOneDocument(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody UpdateOneRequestBody updateOneRequestBody) throws IOException {
        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;
        System.out.println(updateOneRequestBody);
        String jsonObject = updateOneRequestBody.getJsonObject().toString();
        String newContent = updateOneRequestBody.getNewContent().toString();
        System.out.println(jsonObject);
        System.out.println(newContent);
        boolean isDocumentUpdated = updateService.updateOneDocument(databaseName,collectionName,jsonObject,newContent);
        if(isDocumentUpdated)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Document was successfully updated!");

        return ResponseEntityCreator.getResponse(HttpStatus.BAD_REQUEST,"Failed to update! ensure that collection has documents " +
                "and you did not violate the collection schema");
    }

    @PutMapping("node/update/document/{databaseName}/{collectionName}/{id}")
    public ResponseEntity<String> updateDocumentByID(@PathVariable String databaseName, @PathVariable String collectionName, @PathVariable String id, @RequestBody String newContent) throws IOException {
        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;

        boolean isDocumentUpdated = updateService.updateDocumentByID(databaseName,collectionName,id,newContent);
        if(isDocumentUpdated)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Document was successfully updated!");

        return ResponseEntityCreator.getResponse(HttpStatus.BAD_REQUEST,"Failed to update! ensure that you provide an existing document id " +
                "and you did not violate the collection schema");
    }


}
