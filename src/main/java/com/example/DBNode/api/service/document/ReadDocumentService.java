package com.example.DBNode.api.service.document;

import com.example.DBNode.api.model.*;
import com.example.DBNode.cache.CacheLRU;
import com.example.DBNode.database.dao.DAO;
import com.example.DBNode.utils.DocumentMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ReadDocumentService {

    private final DAO dao;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final CacheLRU<String, DocumentsCollection> collectionCache;
    private final IndexingService indexingService;

    @Autowired
    public ReadDocumentService(DAO dao, @Qualifier("collectionCache") CacheLRU<String, DocumentsCollection> collectionCache,
                                IndexingService indexingService) {
        this.dao = dao;
        this.collectionCache = collectionCache;
        this.indexingService = indexingService;
    }

    public DocumentsCollection readCollectionOfDocuments(String databaseName, String collectionName) throws JsonProcessingException {
        Optional<DocumentsCollection> collectionFromCache = this.collectionCache.get(collectionName);
        if(collectionFromCache.isPresent()) {
            System.out.println("Collection from cache");
            return collectionFromCache.get();
        }
        DocumentsCollection collectionFromDAO = dao.readCollection(databaseName,collectionName);
        collectionFromDAO.getIndex().setDocuments(collectionFromDAO.getDocuments());
        collectionFromDAO.getIndex().buildIndex();
        collectionCache.put(collectionName,collectionFromDAO);
        System.out.println("Collection from dao");
        return collectionFromDAO;
    }

    public void removeCollectionFromCache(String collectionName){
        this.collectionCache.evict(collectionName);
    }
    public String readCollectionSchema(String databaseName, String collectionName) throws IOException {
        Document schema = dao.readDocumentByID(databaseName,collectionName,"schema");
        ObjectNode schemaNode = (ObjectNode) objectMapper.readTree(schema.DocumentAsString());
        formatSchemaForClient(schemaNode);
        return schemaNode.toString();
    }
    private void formatSchemaForClient(ObjectNode schemaNode){
        if(schemaNode == null)
            throw new IllegalArgumentException();
        ObjectNode properties = (ObjectNode) schemaNode.get("properties");
        properties.remove("_id");
        schemaNode.replace("properties",properties);
        ArrayNode required = (ArrayNode) schemaNode.get("required");
        for(int i = 0 ; i < required.size(); i++){
            if(required.get(i).asText().equals("_id"))
                required.remove(i);
        }
        schemaNode.replace("required",required);
    }


    public Optional<Document> readDocument(String databaseName, String collectionName, String jsonObject) throws IOException {
        DocumentsCollection collection = readCollectionOfDocuments(databaseName,collectionName);
        List<String> documentsIds = indexingService.searchInIndex(collection,jsonObject);
        if(documentsIds.size() == 0)
            return Optional.empty();
        Optional<Document> document = Optional.ofNullable(readDocumentByID(collection, documentsIds.get(0)));
        if(document.isPresent()){
            ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(document.get().DocumentAsString());
            return Optional.of(formatJsonResponseForClient(jsonNode));
        }
        return Optional.empty();
    }

    public Document readDocumentByID(DocumentsCollection collection, String documentID) {
        if(collection == null || documentID == null)
            throw new IllegalArgumentException();
        System.out.println("Document from Collection");
        return collection.getDocuments().get(documentID);
    }

    private Document formatJsonResponseForClient(ObjectNode jsonNode) throws IOException {
        if(jsonNode == null)
            throw new IllegalArgumentException();
        jsonNode.remove("_id");
        return DocumentMapper.jsonStringToDocument(jsonNode.toString());
    }

    public Optional<List<Document>> readDocuments(String databaseName, String collectionName, String jsonObject) throws JsonProcessingException {
        DocumentsCollection collection = readCollectionOfDocuments(databaseName,collectionName);
        if(jsonObject.equals("")){

        }
        List<String> documentsIds = indexingService.searchInIndex(collection,jsonObject);
        if(documentsIds.size() == 0)
            return Optional.empty();
        return null;
    }

    @PostConstruct
    public void method(){
        System.out.println("ReadService");
        System.out.println(this.dao);
        System.out.println(this.collectionCache);
    }
}
