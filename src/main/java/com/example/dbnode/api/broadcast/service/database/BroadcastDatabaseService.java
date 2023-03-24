package com.example.dbnode.api.broadcast.service.database;

import com.example.dbnode.database.dao.DatabaseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BroadcastDatabaseService {
    private final DatabaseDAO dao;

    public boolean createDatabase(String databaseName){
       return dao.createDatabase(databaseName);
    }

    public boolean deleteDatabase(String databaseName){
        return dao.deleteDatabase(databaseName);
    }
}
