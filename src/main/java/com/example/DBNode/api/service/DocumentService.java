package com.example.DBNode.api.service;

import com.example.DBNode.api.model.Document;
import com.example.DBNode.database.dao.DatabaseDAO;
import com.example.DBNode.utils.DocumentMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final DatabaseDAO dao;

    public boolean addDocument(String databaseName, String collectionName, String jsonObject) throws IOException {
        Document document = DocumentMapper.jsonStringToDocument(jsonObject);
        return dao.addDocument(databaseName,collectionName,document);
    }


    public boolean deleteDocument(String databaseName, String collectionName, String jsonObject) throws IOException {
        Document document = DocumentMapper.jsonStringToDocument(jsonObject);
        return dao.deleteDocument(databaseName,collectionName,document);
    }

    public boolean updateDocument(){
        return false;
    }

    public Document readDocument(){
        return null;
    }

    public String readCollectionSchema(String databaseName, String collectionName) throws IOException {
        Document schema = dao.readDocumentByID(databaseName,collectionName,"schema");
        ObjectNode schemaNode = (ObjectNode) new ObjectMapper().readTree(schema.DocumentAsString());
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

    public List<Document> readDocuments(){
        return null;
    }

    public boolean checkIfDatabaseExist(String databaseName){
        long count = dao.listDatabases().stream().filter(dbName -> dbName.equals(databaseName)).count();
        return (count == 1);
    }

    public boolean checkIfCollectionExist(String databaseName, String collectionName){
        long count = dao.listCollections(databaseName).stream().filter(colName -> colName.equals(collectionName)).count();
        return (count == 1);
    }


}
