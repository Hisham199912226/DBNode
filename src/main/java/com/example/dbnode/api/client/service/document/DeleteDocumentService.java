package com.example.dbnode.api.client.service.document;

import com.example.dbnode.api.bootstrap.model.Node;
import com.example.dbnode.api.broadcast.service.broadcasting.DeleteDocumentBroadcast;
import com.example.dbnode.api.client.model.*;
import com.example.dbnode.api.client.service.IndexingService;
import com.example.dbnode.database.dao.DAO;;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.*;

@Service
@RequiredArgsConstructor
public class DeleteDocumentService {

    private final DAO dao;
    private final ReadDocumentService readService;
    private final IndexingService indexingService;
    private final DeleteDocumentBroadcast broadcast;
    private final WebClient webClient;
    private final Node node;
    private final static  ObjectMapper objectMapper = new ObjectMapper();


    private final Lock deleteLock = new ReentrantLock(true);
    public boolean deleteOneDocument(String databaseName, String collectionName, String jsonObject) throws IOException {
        DocumentsCollection collection;
        boolean[] isDocumentDeleted;
        Document document ;
        try {
            deleteLock.lock();
            collection = readCollection(databaseName,collectionName);
            List<String> documentIds = indexingService.searchInIndex(collection,jsonObject);
            if(isResultEmpty(documentIds)) {
                return false;
            }
            String documentId = documentIds.get(0);
            document = readDocument(collection,documentId);
            isDocumentDeleted = deleteDocument(databaseName,collectionName,document);
            if(isDocumentDeleted[0]){
                deleteDocumentFromCollectionAndIndex(collection,document);
                if(isDocumentDeleted[1] && !databaseName.equals("users"))
                    broadcast.broadcastDeleteDocumentChange(databaseName,collectionName,document.getId());
                return true;
            }
            return false;
        }finally {
            deleteLock.unlock();
        }
    }


    public boolean deleteManyDocuments(String databaseName, String collectionName, String jsonObject) throws IOException {
        DocumentsCollection collection;
        boolean[] isDocumentDeleted;
        try {
            deleteLock.lock();
            collection = readCollection(databaseName,collectionName);
            List<String> documentIds = indexingService.searchInIndex(collection,jsonObject);
            if(isResultEmpty(documentIds))
                return false;
            for(String documentId : documentIds){
                Document document = readDocument(collection,documentId);
                isDocumentDeleted = deleteDocument(databaseName,collectionName,document);
                if(isDocumentDeleted[0]){
                    deleteDocumentFromCollectionAndIndex(collection,document);
                    if(isDocumentDeleted[1])
                        broadcast.broadcastDeleteDocumentChange(databaseName,collectionName,document.getId());
                }
            }
            return true;
        }
        finally {
            deleteLock.unlock();
        }

    }

    public boolean deleteDocumentByID(String databaseName, String collectionName, String documentID) throws JsonProcessingException {
        DocumentsCollection collection;
        Document document;
        try {
            deleteLock.lock();
            collection = readCollection(databaseName, collectionName);
            document = readDocument(collection, documentID);
            if (document == null)
                return false;
            boolean[] isDocumentDeleted = deleteDocument(databaseName, collectionName, document);
            if (isDocumentDeleted[0]) {
                deleteDocumentFromCollectionAndIndex(collection, document);
                if(isDocumentDeleted[1])
                    broadcast.broadcastDeleteDocumentChange(databaseName,collectionName,document.getId());
                return true;
            }
            return false;
        }finally {
            deleteLock.unlock();
        }

        }

    private boolean[] deleteDocument(String databaseName, String collectionName, Document document) throws JsonProcessingException {
        if(databaseName == null || collectionName == null)
            throw new IllegalArgumentException();
        boolean[] result = new boolean[2];
        Node affinityNode = getAffinityNode(document);
        if(affinityNode.equals(node)) {
            result[0] = dao.deleteDocument(databaseName, collectionName, document);
            result[1] = true; // in case of deleting by the same node
        }
        else {
            ResponseEntity<String> response = redirectDeleteQuery(databaseName,collectionName,affinityNode,document.getId());
            result[0] = response.getStatusCode() == HttpStatus.OK;

        }
        return result;
    }

    private Node getAffinityNode(Document document) throws JsonProcessingException {
        if(document == null)
            throw new IllegalArgumentException();
        JsonNode jsonNode = objectMapper.readValue(document.DocumentAsString(),JsonNode.class);
        JsonNode ownerJsonNode = jsonNode.get("owner");
        return objectMapper.treeToValue(ownerJsonNode, Node.class);
    }

    private ResponseEntity<String> redirectDeleteQuery(String databaseName, String collectionName, Node affinityNode, String documentId){
        if(databaseName == null || collectionName == null || affinityNode == null || documentId == null)
            throw new IllegalArgumentException();
        Mono<ResponseEntity<String>> response = webClient.delete()
                .uri(getRedirectDeleteDocumentPath(databaseName,collectionName,affinityNode,documentId))
                .retrieve()
                .toEntity(String.class);
        return response.block();
    }

    private String getRedirectDeleteDocumentPath(String databaseName, String collectionName, Node node, String documentId){
        if(databaseName == null || collectionName == null || node == null || documentId == null)
            throw new IllegalArgumentException();
        return "http://" + node.getIpAddress() + ":" +
                node.getPort() +
                "/node/redirect/delete/document/" +
                databaseName + "/" + collectionName + "?documentId=" + documentId ;
    }

    private DocumentsCollection readCollection(String databaseName, String collectionName) throws JsonProcessingException {
        if(databaseName == null || collectionName == null)
            throw new IllegalArgumentException();
        return readService.readCollectionOfDocuments(databaseName,collectionName);
    }

    private boolean isResultEmpty(List<String> documentsIds){
        if(documentsIds == null)
            throw new IllegalArgumentException();
        return documentsIds.isEmpty();
    }

    private Document readDocument(DocumentsCollection collection,  String documentId) {
        if(collection == null || documentId == null)
            throw new IllegalArgumentException();
        return readService.readDocumentByID(collection,documentId);
    }


    private void deleteDocumentFromCollectionAndIndex(DocumentsCollection collection, Document document) throws JsonProcessingException {
        if(collection == null)
            throw new IllegalArgumentException();
        collection.getDocuments().remove(document.getId());
        collection.getIndex().removeFromIndex(document);
    }
}
