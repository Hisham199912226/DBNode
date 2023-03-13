package com.example.DBNode.database.dao;

import com.example.DBNode.database.io.*;
import com.example.DBNode.model.Document;
import com.example.DBNode.schema.*;
import com.example.DBNode.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseDAO implements DAO {
    private final IO ioOperations = new IOOperations();
    private final String DATA_DEFAULT_PATH = "./data/";
    @Override
    public boolean createDatabase(String databaseName) {
        if(databaseName == null)
            throw new IllegalArgumentException();
        createDefaultPathIfNotExisted();
        return ioOperations.createDirectory(DATA_DEFAULT_PATH,databaseName);
    }

    private boolean createDefaultPathIfNotExisted(){
        File parentDir = new File(DATA_DEFAULT_PATH);
        if(!parentDir.exists())
            return parentDir.mkdir();
        return false;
    }
    @Override
    public boolean deleteDatabase(String databaseName) {
        if(databaseName == null)
            throw new IllegalArgumentException();
        return ioOperations.deleteDirectory(DATA_DEFAULT_PATH,databaseName);
    }

    @Override
    public boolean createCollection(String databaseName, String collectionName) {
        if(databaseName == null || collectionName == null)
            throw new IllegalArgumentException();
        createDefaultPathIfNotExisted();
        return ioOperations.createDirectory(constructPath(databaseName),collectionName);
    }

    private String constructPath(String databaseName){
        if(databaseName == null)
            throw new IllegalArgumentException();
        return "./data/".concat(databaseName);
    }

    @Override
    public boolean deleteCollection(String databaseName, String collectionName) {
        if(databaseName == null || collectionName == null)
            throw new IllegalArgumentException();
        return ioOperations.deleteDirectory(constructPath(databaseName),collectionName);
    }

    @Override
    public boolean addDocument(String databaseName, String collectionName, Document document) throws IOException {
        if(databaseName == null || collectionName == null || document == null)
            throw new IllegalArgumentException();
        String documentID = DocumentIDGenerator.getUniqueID();
        String jsonObjectAsString = DocumentMapper.DocumentToJsonString(document);
        document.getDocument().put("_id", documentID);
        document.setId(documentID);
        String path = constructPath(databaseName,collectionName);
        if(isCollectionEmpty(databaseName,collectionName)) {
            createSchemaFile(path,jsonObjectAsString);
            return createDocumentFile(path,documentID,jsonObjectAsString);
        }
        else {
            String jsonSchema = getSchema(path);
            if(isJsonObjectValid(jsonSchema,jsonObjectAsString)){
                return createDocumentFile(path,documentID,jsonObjectAsString);
            }
        }
        return false;
    }
    private String constructPath(String databaseName, String collectionName){
        if(databaseName == null || collectionName == null)
            throw new IllegalArgumentException();
        return DATA_DEFAULT_PATH.concat(databaseName.concat("/").concat(collectionName));
    }
    private boolean isCollectionEmpty(String databaseName, String collectionName){
        return countDocuments(databaseName,collectionName) == 0;
    }
    private boolean createSchemaFile(String path, String jsonObjectAsString) throws IOException {
        String jsonSchema = produceJsonSchema(jsonObjectAsString);
        return ioOperations.createFile(path,"schema",jsonSchema);
    }
    private String produceJsonSchema(String jsonObjectAsString) throws JsonProcessingException {
        return JsonSchemaProducer.generateJsonSchema(jsonObjectAsString);
    }
    private boolean createDocumentFile(String path, String documentID, String jsonObjectAsString) throws IOException {
        return ioOperations.createFile(path,documentID,jsonObjectAsString);
    }
    private String getSchema(String path) throws IOException {
        File file = ioOperations.getFileByName(path,"schema");
        return DocumentMapper.FileToDocument(file).DocumentAsString();
    }
    private boolean isJsonObjectValid(String jsonSchema, String jsonObject){
       return JsonSchemaValidator.validateJsonObject(jsonSchema,jsonObject);
    }



    @Override
    public boolean deleteDocument(String databaseName, String collectionName, Document document) {
        if(databaseName == null || collectionName == null || document == null)
            throw new IllegalArgumentException();
        String path = constructPath(databaseName,collectionName);
        return ioOperations.deleteFile(path,document.getId());
    }

    @Override
    public boolean updateDocument(String databaseName, String collectionName, Document document) {
        return false;
    }

    @Override
    public List<String> listDatabases() {
        return ioOperations.getDirectoriesNames(DATA_DEFAULT_PATH,"");
    }

    @Override
    public List<String> listCollections(String databaseName) {
        return ioOperations.getDirectoriesNames(DATA_DEFAULT_PATH,databaseName);
    }

    @Override
    public int countDocuments(String databaseName, String collectionName) {
        int documentsCount =  ioOperations.getFilesCount(constructPath(databaseName),collectionName);
        return (documentsCount == 0 ? 0 : documentsCount -1);
    }

    @Override
    public List<Document> readCollection(String databaseName, String collectionName) {
        String path = constructPath(databaseName);
        return ioOperations.getFiles(path,collectionName).stream().filter(file -> !file.getName().equalsIgnoreCase("schema.txt")).map(file -> {
            try {
                Document document = DocumentMapper.FileToDocument(file);
                String fileName = file.getName();
                String documentId = fileName.substring(0,fileName.indexOf("."));
                document.setId(documentId);
                return document;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public Document readDocumentByID(String databaseName, String collectionName, String documentID) throws IOException {
        if(databaseName == null || collectionName == null || documentID == null)
            throw new IllegalArgumentException();
        String path = constructPath(databaseName,collectionName);
        File file = ioOperations.getFileByName(path,documentID);
        return DocumentMapper.FileToDocument(file);
    }

    @Override
    public List<Document> readDocumentsByIDs(String databaseName, String collectionName, List<String> documentIDs) {
        if(databaseName == null || collectionName == null || documentIDs == null)
            throw new IllegalArgumentException();
        return documentIDs.stream().map(documentID -> {
            try {
                Document document = readDocumentByID(databaseName,collectionName,documentID);
                document.setId(documentID);
                return document;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }


}
