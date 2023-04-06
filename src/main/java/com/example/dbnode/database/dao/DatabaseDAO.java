package com.example.dbnode.database.dao;

import com.example.dbnode.database.io.*;
import com.example.dbnode.api.client.model.*;
import com.example.dbnode.schema.*;
import com.example.dbnode.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
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
    public boolean generateIdAndAddDocument(String databaseName, String collectionName, Document document) throws IOException {
        if(databaseName == null || collectionName == null || document == null)
            throw new IllegalArgumentException();
        String documentID = DocumentIDGenerator.getUniqueID();
        document.getDocument().put("_id", documentID);
        document.setId(documentID);
        return addDocument(databaseName,collectionName,document);
    }

    @Override
    public boolean addDocument(String databaseName, String collectionName, Document document) throws IOException {
        if(databaseName == null || collectionName == null || document == null)
            throw new IllegalArgumentException();
        String jsonObjectAsString = DocumentMapper.documentToJsonString(document);
        String path = constructPath(databaseName,collectionName);
        String jsonSchema = getSchema(path);
        if(isJsonObjectValid(jsonSchema,jsonObjectAsString)){
            return createDocumentFile(path,document.getId(),jsonObjectAsString);
        }
        return false;
    }

    private String constructPath(String databaseName, String collectionName){
        if(databaseName == null || collectionName == null)
            throw new IllegalArgumentException();
        return DATA_DEFAULT_PATH.concat(databaseName.concat("/").concat(collectionName));
    }

    public boolean createSchemaFile(String databaseName, String collectionName, String jsonObjectAsString) throws IOException {
        String jsonSchema = produceJsonSchema(jsonObjectAsString);
        String path = constructPath(databaseName,collectionName);
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
        return DocumentMapper.fileToDocument(file).DocumentAsString();
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
    public boolean updateDocument(String databaseName, String collectionName, Document oldDocument, Document newDocument) throws IOException {
        if(databaseName == null || collectionName == null || oldDocument == null || newDocument == null)
            throw new IllegalArgumentException();
        String path = constructPath(databaseName,collectionName);
        String oldDocumentID = oldDocument.getId();
        String newContent = DocumentMapper.documentToJsonString(newDocument);
        return ioOperations.updateFile(path,oldDocumentID,newContent);
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
        return (documentsCount == 0 ? 0 : documentsCount - 1);
    }

    @Override
    public DocumentsCollection readCollection(String databaseName, String collectionName) {
        String path = constructPath(databaseName);
        List<File> files = ioOperations.getFiles(path, collectionName);
        String extension = ".txt";
        List<Document> documents = files.parallelStream()
                .map(file -> {
                    String fileName = file.getName();
                    if (!fileName.endsWith(extension) || fileName.equalsIgnoreCase("schema.txt")) {
                        return null; // skip this file
                    }
                    String documentId = fileName.substring(0, fileName.length() - extension.length());
                    try {
                        Document document = DocumentMapper.fileToDocument(file);
                        document.setId(documentId);
                        return document;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        DocumentsCollection collection = new DocumentsCollection();
        collection.setCollectionName(collectionName);
        collection.setDocuments((ConcurrentHashMap<String, Document>) documents.stream()
                .collect(Collectors.toConcurrentMap(Document::getId, Document::getReference)));
        return collection;
    }

    @Override
    public Document readDocumentByID(String databaseName, String collectionName, String documentID) throws IOException {
        if(databaseName == null || collectionName == null || documentID == null)
            throw new IllegalArgumentException();
        String path = constructPath(databaseName,collectionName);
        File file = ioOperations.getFileByName(path,documentID);
        Document document = DocumentMapper.fileToDocument(file);
        document.setId(documentID);
        return document;
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
