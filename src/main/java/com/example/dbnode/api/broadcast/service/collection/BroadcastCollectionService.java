package com.example.dbnode.api.broadcast.service.collection;

import com.example.dbnode.database.dao.DatabaseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BroadcastCollectionService {
    private final DatabaseDAO dao;

    public boolean createCollection(String databaseName, String collectionName){
        return dao.createCollection(databaseName,collectionName);
    }

    public boolean deleteCollection(String databaseName, String collectionName){
        return dao.deleteCollection(databaseName,collectionName);
    }
}
