package com.example.dbnode.api.client.controller.document;

import com.example.dbnode.api.client.service.PathValidationService;
import com.example.dbnode.api.client.service.document.ReadDocumentService;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ReadDocumentController {
    private final ReadDocumentService readService;
    private final PathValidationService pathValidationService;

    @PostMapping("node/client/read/document/one/{databaseName}/{collectionName}")
    public ResponseEntity<String> readOneDocument(Principal principal, @PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject) throws IOException {
        if(databaseName.equals("db_system") && hasRole(principal,"USER"))
            return ResponseEntityCreator.getResponse(HttpStatus.UNAUTHORIZED,"you cannot read from db_system as user");
        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;
        Optional<String> jsonResponseString = readService.readDocument(databaseName,collectionName,jsonObject);
        return jsonResponseString.map(s -> ResponseEntityCreator.getResponse(HttpStatus.OK, s)).orElseGet(()
                -> ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND, "Document not found!"));
    }

    @PostMapping("node/client/read/document/many/{databaseName}/{collectionName}")
    public ResponseEntity<String> readManyDocuments(Principal principal, @PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject) throws IOException {
        if(databaseName.equals("db_system") && hasRole(principal,"USER"))
            return ResponseEntityCreator.getResponse(HttpStatus.UNAUTHORIZED,"you cannot read from db_system as user");
        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;
        Optional<String> jsonResponseString = readService.readDocuments(databaseName,collectionName,jsonObject);
        return jsonResponseString.map(s -> ResponseEntityCreator.getResponse(HttpStatus.OK, s)).orElseGet(()
                -> ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND, "Documents not found!"));
    }

    @GetMapping("node/client/read/document/count/{databaseName}/{collectionName}")
    public ResponseEntity<String> countDocuments(Principal principal, @PathVariable String databaseName, @PathVariable String collectionName){
        if(databaseName.equals("db_system") && hasRole(principal,"USER"))
            return ResponseEntityCreator.getResponse(HttpStatus.UNAUTHORIZED,"you cannot read from db_system as user");
        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;
        String documentsCount = readService.countDocuments(databaseName,collectionName);
        return ResponseEntityCreator.getResponse(HttpStatus.OK,documentsCount);
    }

    private boolean hasRole(Principal principal, String roleName) {
        if (principal instanceof Authentication) {
            Authentication authentication = (Authentication) principal;
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(roleName));
        }
        return false;
    }

}
