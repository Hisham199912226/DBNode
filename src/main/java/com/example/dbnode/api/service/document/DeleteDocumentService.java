package com.example.dbnode.api.service.document;

import com.example.dbnode.api.model.Document;
import com.example.dbnode.api.model.DocumentsCollection;
import com.example.dbnode.api.service.IndexingService;
import com.example.dbnode.database.dao.DAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class DeleteDocumentService {

    private final DAO dao;
    private final ReadDocumentService readService;
    private final IndexingService indexingService;

    private final Lock deleteLock = new ReentrantLock(true);
    public boolean deleteOneDocument(String databaseName, String collectionName, String jsonObject) throws IOException {
        DocumentsCollection collection;
        boolean isDocumentDeleted;
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
            if(isDocumentDeleted){
                deleteDocumentFromCollectionAndIndex(collection,document);
                return true;
            }
            return false;
        }finally {
            deleteLock.unlock();
        }
    }


    public boolean deleteManyDocuments(String databaseName, String collectionName, String jsonObject) throws IOException {
        DocumentsCollection collection;
        boolean isDocumentDeleted;
        try {
            deleteLock.lock();
            collection = readCollection(databaseName,collectionName);
            List<String> documentIds = indexingService.searchInIndex(collection,jsonObject);
            if(isResultEmpty(documentIds))
                return false;
            for(String documentId : documentIds){
                Document document = readDocument(collection,documentId);
                isDocumentDeleted = deleteDocument(databaseName,collectionName,document);
                if(isDocumentDeleted){
                    deleteDocumentFromCollectionAndIndex(collection,document);
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
            collection = readCollection(databaseName,collectionName);
            document = readDocument(collection,documentID);
            if(document == null)
                return false;
            boolean isDocumentDeleted = deleteDocument(databaseName,collectionName,document);
            if(isDocumentDeleted){
                deleteDocumentFromCollectionAndIndex(collection,document);
                return true;
            }
            return false;
        }

    private boolean deleteDocument(String databaseName, String collectionName, Document document){
        if(databaseName == null || collectionName == null)
            throw new IllegalArgumentException();
        return dao.deleteDocument(databaseName,collectionName,document);
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
