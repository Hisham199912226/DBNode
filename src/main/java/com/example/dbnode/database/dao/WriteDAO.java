package com.example.dbnode.database.dao;

import com.example.dbnode.api.client.model.Document;

import java.io.IOException;


public interface WriteDAO {
    boolean createDatabase(String databaseName);
    boolean deleteDatabase(String databaseName);
    boolean createCollection(String databaseName, String collectionName);
    boolean deleteCollection(String databaseName, String collectionName);
    boolean generateIdAndAddDocument(String databaseName, String collectionName, Document document) throws IOException;
    boolean addDocument(String databaseName, String collectionName, Document document) throws IOException;
    boolean deleteDocument(String databaseName, String collectionName, Document document);
    boolean updateDocument(String databaseName, String collectionName, Document oldDocument, Document newDocument) throws IOException;

}
