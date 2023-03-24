package com.example.dbnode.database.dao;



import com.example.dbnode.api.client.model.Document;
import com.example.dbnode.api.client.model.DocumentsCollection;

import java.io.IOException;
import java.util.List;
public interface ReadDAO {
    List<String> listDatabases();
    List<String> listCollections(String databaseName);
    int countDocuments(String databaseName, String collectionName);
    DocumentsCollection readCollection(String databaseName, String collectionName);
    Document readDocumentByID(String databaseName, String collectionName,String documentID) throws IOException;
    List<Document> readDocumentsByIDs(String databaseName, String collectionName,List<String> documentIDs);
}
