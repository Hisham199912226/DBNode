package com.example.dbnode.api.broadcast.service.document;

import com.example.dbnode.api.client.model.*;
import com.example.dbnode.api.client.service.document.ReadDocumentService;
import com.example.dbnode.database.dao.DAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BroadcastDeleteDocumentService {

    private final DAO dao;
    private final ReadDocumentService readService;

    public boolean deleteOneDocument(String databaseName, String collectionName, String id) throws JsonProcessingException {
        return deleteDocumentByID(databaseName,collectionName,id);
    }

    public boolean deleteManyDocuments(String databaseName, String collectionName, List<String> ids) throws JsonProcessingException {
        for (String id : ids){
            boolean deleteResult = deleteDocumentByID(databaseName,collectionName,id);
            if(!deleteResult)
                return false;
        }
        return true;
    }

    private boolean deleteDocumentByID(String databaseName, String collectionName, String documentID) throws JsonProcessingException {
        DocumentsCollection collection;
        Document document;
        collection = readCollection(databaseName, collectionName);
        document = readDocument(collection, documentID);
        if (document == null)
            return false;
        boolean isDocumentDeleted = deleteDocument(databaseName, collectionName, document);
        if (isDocumentDeleted) {
            deleteDocumentFromCollectionAndIndex(collection, document);
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
