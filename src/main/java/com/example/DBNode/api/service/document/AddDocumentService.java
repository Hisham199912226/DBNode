package com.example.DBNode.api.service.document;

import com.example.DBNode.api.model.Document;
import com.example.DBNode.database.dao.DatabaseDAO;
import com.example.DBNode.utils.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AddDocumentService {
    private final DatabaseDAO dao;
    private final ReadDocumentService readService;
    public boolean addDocument(String databaseName, String collectionName, String jsonObject) throws IOException {
        Document document = DocumentMapper.jsonStringToDocument(jsonObject);
        document.getDocument().put("version",1);
        readService.removeCollectionFromCache(collectionName);
        return dao.addDocument(databaseName,collectionName,document);
    }
}
