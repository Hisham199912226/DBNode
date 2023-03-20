package com.example.dbnode.api.service.document;

import com.example.dbnode.api.model.Document;
import com.example.dbnode.database.dao.DatabaseDAO;
import com.example.dbnode.utils.DocumentMapper;
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
