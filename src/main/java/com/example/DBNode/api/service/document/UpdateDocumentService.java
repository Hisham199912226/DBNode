package com.example.DBNode.api.service.document;

import com.example.DBNode.api.model.Document;
import com.example.DBNode.api.model.DocumentsCollection;
import com.example.DBNode.database.dao.DAO;
import com.example.DBNode.utils.DocumentMapper;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class UpdateDocumentService {

    private final DAO dao;
    private final ReadDocumentService readService;
    private final IndexingService indexingService;
    private static final ReentrantLock lock = new ReentrantLock(true);
    private static final ObjectMapper mapper = new ObjectMapper();


    public boolean updateOneDocument(String databaseName, String collectionName, String jsonObject, String newContent) throws IOException {
        DocumentsCollection collection = readService.readCollectionOfDocuments(databaseName,collectionName);
        List<String> documentIds = indexingService.searchInIndex(collection,jsonObject);
        System.out.println(documentIds);
        System.out.println(collection.getIndex());
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
        if (document == null) {
            return false;
        }
        int currentVersion = (int) document.getDocument().get("version");
        System.out.println(Thread.currentThread().getName() +  " " + currentVersion);

        JsonNode newDoc = mapper.readTree(document.DocumentAsString());
        JsonNode content = mapper.readTree(newContent);
        boolean success = traverseToUpdateLocally(newDoc, content);
        if (!success) {
            return false;
        }

        boolean updated = tryUpdateDocument(databaseName, collectionName, currentVersion, newDoc,documentID);
        if (!updated) {
            System.out.println(Thread.currentThread().getName() + " Try again!");
            return updateDocumentByID(databaseName, collectionName, documentID, newContent);
        }
        return true;
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
                System.out.println(Thread.currentThread().getName() + " " + currentVersion);
                ((ObjectNode) newDoc).put("version", newDoc.get("version").asInt() + 1);
                readService.removeCollectionFromCache(collectionName);
                dao.updateDocument(databaseName, collectionName, document, DocumentMapper.jsonStringToDocument(newDoc.toString()));
                System.out.println("document before updated" + document);
                return true;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

}
