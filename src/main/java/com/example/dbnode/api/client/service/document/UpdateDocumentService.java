package com.example.dbnode.api.client.service.document;

import com.example.dbnode.api.bootstrap.model.Node;
import com.example.dbnode.api.broadcast.service.broadcasting.UpdateDocumentBroadcast;
import com.example.dbnode.api.client.model.*;
import com.example.dbnode.api.client.service.IndexingService;
import com.example.dbnode.database.dao.DAO;
import com.example.dbnode.utils.DocumentMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class UpdateDocumentService {

    private final DAO dao;
    private final ReadDocumentService readService;
    private final IndexingService indexingService;
    private final UpdateDocumentBroadcast broadcast;
    private static final ReentrantLock lock = new ReentrantLock(true);
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Node node;

    private final WebClient webClient;


    public boolean updateOneDocument(String databaseName, String collectionName, String jsonObject, String newContent) throws IOException {
        DocumentsCollection collection = readService.readCollectionOfDocuments(databaseName,collectionName);
        List<String> documentIds = indexingService.searchInIndex(collection,jsonObject);
        if(isResultEmpty(documentIds)) {
            return false;
        }
        String documentID = documentIds.get(0);
        return updateDocumentByID(databaseName,collectionName,documentID,newContent);
    }
    private boolean isResultEmpty(List<String> documentsIds){
        if(documentsIds == null)
            throw new IllegalArgumentException();
        return documentsIds.size() == 0;
    }



    public boolean updateDocumentByID(String databaseName, String collectionName, String documentID, String newContent) throws IOException {
        DocumentsCollection collection = readService.readCollectionOfDocuments(databaseName,collectionName);
        Document document = collection.getDocuments().get(documentID);
        System.out.println("Collection was read");
        if (document == null) {
            return false;
        }
        System.out.println("document was read");
        int currentVersion = (int) document.getDocument().get("version");
        Node affinityNode = getAffinityNode(document);
        System.out.println("affinityNode " + affinityNode);
        if(!affinityNode.getNodeName().equals(node.getNodeName())){
            ResponseEntity<String> response = redirectUpdateQuery(databaseName,collectionName,affinityNode,documentID,newContent);
            return response.getStatusCode() == HttpStatus.OK;
        }
        JsonNode newDoc = mapper.readTree(document.DocumentAsString());
        JsonNode content = mapper.readTree(newContent);
        boolean success = traverseToUpdateLocally(newDoc, content);
        if (!success) {
            return false;
        }

        boolean updated = tryUpdateDocument(databaseName, collectionName, currentVersion, newDoc,documentID);
        if (!updated) {
            return updateDocumentByID(databaseName, collectionName, documentID, newContent);
        }
        broadcast.broadcastUpdateDocumentChange(databaseName,collectionName,newDoc.toString(),documentID);
        return true;
    }

    private Node getAffinityNode(Document document) throws JsonProcessingException {
        if(document == null)
            throw new IllegalArgumentException();
        JsonNode jsonNode = mapper.readValue(document.DocumentAsString(),JsonNode.class);
        JsonNode ownerJsonNode = jsonNode.get("owner");
        System.out.println("getAffinityNode -- > " + ownerJsonNode);
        return mapper.treeToValue(ownerJsonNode, Node.class);
    }

    private ResponseEntity<String> redirectUpdateQuery(String databaseName, String collectionName, Node affinityNode, String documentId, String newContent) {
        if(databaseName == null || collectionName == null || affinityNode == null || documentId == null || newContent == null)
            throw new IllegalArgumentException();
        System.out.println("redirectUpdateQuery");
        Mono<ResponseEntity<String>> response = webClient.put()
                .uri(getRedirectUpdateDocumentPath(databaseName,collectionName,affinityNode,documentId))
                .bodyValue(newContent)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.empty())
                .toEntity(String.class);
        return response.block();
    }

    private String getRedirectUpdateDocumentPath(String databaseName, String collectionName, Node node, String documentId){
        if(databaseName == null || collectionName == null || node == null || documentId == null)
            throw new IllegalArgumentException();
        String path =  "http://" + node.getNodeName() + ":" +
                node.getPort() +
                "/node/redirect/update/document/" +
                databaseName + "/" + collectionName + "?documentId=" + documentId ;
        System.out.println("PAth : " + path);
        return path;
    }


    private boolean updateObjectProperties(JsonNode newDoc, JsonNode content) {
        if(newDoc == null || content == null)
            throw new IllegalArgumentException();
        boolean success = true;
        Iterator<String> fieldNames = content.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode newProperty = content.get(fieldName);
            if (newDoc.has(fieldName)) {
                JsonNode oldProperty = newDoc.get(fieldName);
                if (oldProperty.isObject() && newProperty.isObject()) {
                    success &= traverseToUpdateLocally(oldProperty, newProperty);
                } else {
                    ((ObjectNode) newDoc).replace(fieldName, newProperty);
                }
            } else {
                success = false;
            }
        }
        return success;
    }
    private boolean updateArrayElements(JsonNode newDoc, JsonNode content) {
        if(newDoc == null || content == null)
            throw new IllegalArgumentException();
        boolean success = true;
        for (int i = 0; i < content.size(); i++) {
            JsonNode newElement = content.get(i);
            if (newDoc.size() > i) {
                JsonNode oldElement = newDoc.get(i);
                if (oldElement.isObject() && newElement.isObject()) {
                    success &= traverseToUpdateLocally(oldElement, newElement);
                } else {
                    ((ArrayNode) newDoc).set(i, newElement);
                }
            } else {
                ((ArrayNode) newDoc).add(newElement);
            }
        }
        return success;
    }

    private boolean traverseToUpdateLocally(JsonNode newDoc, JsonNode content) {
        if (newDoc == null || content == null) {
            throw new IllegalArgumentException("Both old and new documents must be non-null");
        }
        boolean success;
        if (content.isObject()) {
            success = updateObjectProperties(newDoc, content);
        } else if (content.isArray()) {
            success = updateArrayElements(newDoc, content);
        } else {
            success = updateSingleNode(newDoc, content);
        }
        return success;
    }
    private boolean updateSingleNode(JsonNode newDoc, JsonNode content) {
        if(newDoc == null || content == null)
            throw new IllegalArgumentException();
        ((ObjectNode) newDoc).replace("", content);
        return true;
    }
    private boolean tryUpdateDocument(String databaseName, String collectionName, int currentVersion, JsonNode newDoc, String documentID) throws IOException {
        try {
            lock.lock();
            DocumentsCollection collection = readService.readCollectionOfDocuments(databaseName, collectionName);
            Document document = collection.getDocuments().get(documentID);
            if (currentVersion == (int) document.getDocument().get("version")) {
                ((ObjectNode) newDoc).put("version", newDoc.get("version").asInt() + 1);
                readService.removeCollectionFromCache(collectionName);
                dao.updateDocument(databaseName, collectionName, document, DocumentMapper.jsonStringToDocument(newDoc.toString()));
                return true;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

}
