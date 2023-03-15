package com.example.DBNode.api.service;

import com.example.DBNode.database.dao.DatabaseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollectionService {
    private final DatabaseDAO dao;

    public boolean createCollection(String databaseName, String collectionName){
        return dao.createCollection(databaseName,collectionName);
    }

    public boolean deleteCollection(String databaseName, String collectionName){
        return dao.deleteCollection(databaseName,collectionName);
    }

    public boolean checkIfDatabaseExist(String databaseName){
        long count = dao.listDatabases().stream().filter(dbName -> dbName.equals(databaseName)).count();
        return (count == 1);
    }
}
