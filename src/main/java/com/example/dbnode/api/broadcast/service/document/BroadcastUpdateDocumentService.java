package com.example.dbnode.api.broadcast.service.document;

import com.example.dbnode.api.client.model.Document;
import com.example.dbnode.database.dao.DAO;
import com.example.dbnode.utils.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BroadcastUpdateDocumentService {

    private final DAO dao;

    public boolean broadcastUpdateDocumentChange(String databaseName, String collectionName, String newContent, String id) throws IOException {
        Document oldDocument = new Document();
        oldDocument.setId(id);
        Document newDocument = DocumentMapper.jsonStringToDocument(newContent);
        return dao.updateDocument(databaseName,collectionName,oldDocument,newDocument);
    }
}
