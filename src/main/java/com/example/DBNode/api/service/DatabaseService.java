package com.example.DBNode.api.service;

import com.example.DBNode.database.dao.DatabaseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatabaseService {
    private final DatabaseDAO dao;

    public boolean createDatabase(String databaseName){
        return dao.createDatabase(databaseName);
    }

    public boolean deleteDatabase(String databaseName){
        return dao.deleteDatabase(databaseName);
    }
}
