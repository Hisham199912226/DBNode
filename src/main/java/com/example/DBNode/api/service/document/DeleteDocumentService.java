package com.example.DBNode.api.service.document;

import com.example.DBNode.api.model.Document;
import com.example.DBNode.api.model.DocumentsCollection;
import com.example.DBNode.database.dao.DAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeleteDocumentService {

    private final DAO dao;
    private final ReadDocumentService readService;
    private final IndexingService indexingService;

    public boolean deleteOneDocument(String databaseName, String collectionName, String jsonObject) throws IOException {
        DocumentsCollection collection = readCollection(databaseName,collectionName);
        List<String> documentIds = indexingService.searchInIndex(collection,jsonObject);
        if(isResultEmpty(documentIds))
            return false;
        String documentId = documentIds.get(0);
        Document document = readDocument(collection,documentId);
        boolean isDocumentDeleted = deleteDocument(databaseName,collectionName,document);
        if(isDocumentDeleted){
            deleteDocumentFromCollectionAndIndex(collection,document);
            return true;
        }
        return false;
    }


    public boolean deleteManyDocuments(String databaseName, String collectionName, String jsonObject) throws IOException {
        DocumentsCollection collection = readCollection(databaseName,collectionName);
        List<String> documentIds = indexingService.searchInIndex(collection,jsonObject);
        if(isResultEmpty(documentIds))
            return false;
        for(String documentId : documentIds){
            Document document = readDocument(collection,documentId);
            boolean isDocumentDeleted = deleteDocument(databaseName,collectionName,document);
            if(isDocumentDeleted){
                deleteDocumentFromCollectionAndIndex(collection,document);
            }
        }
        return true;
    }

    private DocumentsCollection readCollection(String databaseName, String collectionName) throws JsonProcessingException {
        if(databaseName == null || collectionName == null)
            throw new IllegalArgumentException();
        return readService.readCollectionOfDocuments(databaseName,collectionName);
    }

    private boolean isResultEmpty(List<String> documentsIds){
        if(documentsIds == null)
            throw new IllegalArgumentException();
        return documentsIds.size() == 0;
    }

    private Document readDocument(DocumentsCollection collection,  String documentId) throws IOException {
        if(collection == null || documentId == null)
            throw new IllegalArgumentException();
        return readService.readDocumentByID(collection,documentId);
    }

    private boolean deleteDocument(String databaseName, String collectionName, Document document){
        if(databaseName == null || collectionName == null)
            throw new IllegalArgumentException();
        return dao.deleteDocument(databaseName,collectionName,document);
    }
    private void deleteDocumentFromCollectionAndIndex(DocumentsCollection collection, Document document) throws JsonProcessingException {
        if(collection == null)
            throw new IllegalArgumentException();
        collection.getDocuments().remove(document.getId());
        collection.getIndex().removeFromIndex(document);
    }
}
