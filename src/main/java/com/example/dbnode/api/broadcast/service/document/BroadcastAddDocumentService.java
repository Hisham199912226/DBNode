package com.example.dbnode.api.broadcast.service.document;

import com.example.dbnode.api.client.model.Document;
import com.example.dbnode.api.client.service.document.ReadDocumentService;
import com.example.dbnode.database.dao.DatabaseDAO;
import com.example.dbnode.utils.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BroadcastAddDocumentService {
    private final DatabaseDAO dao;
    private final ReadDocumentService readService;

    public boolean addDocument(String databaseName, String collectionName, String jsonObject, String id) throws IOException {
        Document document = DocumentMapper.jsonStringToDocument(jsonObject);
        document.getDocument().put("_id",id);
        document.getDocument().put("version",1);
        document.setId(id);
        readService.removeCollectionFromCache(collectionName);
        return dao.addDocument(databaseName,collectionName,document);
    }
}
