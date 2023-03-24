package com.example.dbnode.api.client.service;

import com.example.dbnode.database.dao.DAO;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathValidationService {
    private final DAO dao;

    public ResponseEntity<String> checkPath(String databaseName, String collectionName){
        if(!checkIfDatabaseExist(databaseName))
            return ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND,"Database you provided does not exist!");
        if(!checkIfCollectionExist(databaseName,collectionName))
            return ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND,"Collection you provided does not exist!");
        return ResponseEntity.ok().body("");
    }

    private boolean checkIfDatabaseExist(String databaseName){
        long count = dao.listDatabases().stream().filter(dbName -> dbName.equals(databaseName)).count();
        return (count == 1);
    }

    private boolean checkIfCollectionExist(String databaseName, String collectionName){
        long count = dao.listCollections(databaseName).stream().filter(colName -> colName.equals(collectionName)).count();
        return (count == 1);
    }
}
