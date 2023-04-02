package com.example.dbnode.api.broadcast.service.collection;

import com.example.dbnode.database.dao.DatabaseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BroadcastCollectionService {
    private final DatabaseDAO dao;

    public boolean createCollection(String databaseName, String collectionName,String jsonSchema) throws IOException {
        boolean isCollectionCreated = dao.createCollection(databaseName,collectionName);
        if(isCollectionCreated){
            dao.createSchemaFile(databaseName,collectionName,jsonSchema);
        }
        return isCollectionCreated;
    }

    public boolean deleteCollection(String databaseName, String collectionName){
        return dao.deleteCollection(databaseName,collectionName);
    }
}
