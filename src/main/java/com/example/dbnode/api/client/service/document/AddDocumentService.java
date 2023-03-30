package com.example.dbnode.api.client.service.document;

import com.example.dbnode.api.bootstrap.model.Node;
import com.example.dbnode.api.broadcast.service.broadcasting.AddDocumentBroadcast;
import com.example.dbnode.api.client.model.Document;
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
    private final AddDocumentBroadcast broadcast;
    private final Node node;

    public boolean generateIdAndAddDocument(String databaseName, String collectionName, String jsonObject) throws IOException {
        Document document = DocumentMapper.jsonStringToDocument(jsonObject);
        if(isNotUserDatabase(databaseName)) {
            document.getDocument().put("version", 1);
            document.getDocument().put("owner", node);
        }
        readService.removeCollectionFromCache(collectionName);
        boolean isDocumentCreated = dao.generateIdAndAddDocument(databaseName,collectionName,document);
        if(isDocumentCreated) {
            if(isNotUserDatabase(databaseName))
                broadcast.broadcastAddDocumentChange(databaseName,collectionName,DocumentMapper.documentToJsonString(document),document.getId());
            return true;
        }
        return false;
    }

    private boolean isNotUserDatabase(String databaseName){
        if(databaseName == null)
            throw new IllegalArgumentException();
        return !databaseName.equals("db_system");
    }


}
