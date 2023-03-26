package com.example.dbnode.api.client.controller.document;

import com.example.dbnode.api.bootstrap.model.Node;
import com.example.dbnode.api.bootstrap.service.AffinityService;
import com.example.dbnode.api.client.service.PathValidationService;
import com.example.dbnode.api.client.service.document.AddDocumentService;
import com.example.dbnode.api.client.service.document.ReadDocumentService;

import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class AddDocumentController {

    private final WebClient webClient;
    private final AddDocumentService addService;
    private final ReadDocumentService readService;
    private final PathValidationService pathValidationService;
    private final AffinityService affinityService;
    private final Node node;


    @PostMapping("node/client/add/document/{databaseName}/{collectionName}")
    public ResponseEntity<String> addDocument(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject) throws IOException {
        ResponseEntity<String> response = pathValidationService.checkPath(databaseName,collectionName);
        if(response.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return response;
        Node affinityNode = affinityService.getWriteAffinity();
        if(!isMyAffinityToWrite(affinityNode)){
            System.out.println("redirect query");
            System.out.println("Affinity node : " + affinityNode);
            return redirectAddQuery(databaseName,collectionName,affinityNode,jsonObject);
        }
        boolean isDocumentCreated = addService.generateIdAndAddDocument(databaseName,collectionName,jsonObject);
        if(isDocumentCreated)
            return ResponseEntityCreator.getResponse(HttpStatus.CREATED,"Document was successfully created!");
        String collectionSchema = readService.readCollectionSchema(databaseName,collectionName);
        return ResponseEntityCreator.getResponse(HttpStatus.NOT_ACCEPTABLE,"Document failed to pass schema validation!\n".concat(collectionSchema));
    }

    private boolean isMyAffinityToWrite(Node affinityNode){
        return affinityNode.equals(node);
    }
    public ResponseEntity<String> redirectAddQuery(String databaseName, String collectionName, Node affinityNode, String jsonObject){
            Mono<ResponseEntity<String>> response = webClient.post()
                    .uri(getRedirectAddDocumentPath(databaseName,collectionName,affinityNode))
                    .bodyValue(jsonObject)
                    .retrieve()
                    .toEntity(String.class);
            return response.block();
    }

    private String getRedirectAddDocumentPath(String databaseName, String collectionName, Node node){
        return "http://" + node.getIpAddress() + ":" +
                node.getPort() +
                "/node/redirect/add/document/" +
                databaseName + "/" + collectionName;
    }
}
