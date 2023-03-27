package com.example.dbnode.api.client.service.database;

import com.example.dbnode.api.broadcast.service.broadcasting.DatabaseBroadcast;
import com.example.dbnode.database.dao.DatabaseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DatabaseService {
    private final DatabaseDAO dao;
    private final DatabaseBroadcast broadcast;
    public boolean createDatabase(String databaseName){
        boolean isDatabaseCreated = dao.createDatabase(databaseName);
        if(isDatabaseCreated) {
            if(!databaseName.equals("db_system"))
                broadcast.broadcastCreateDatabaseChange(databaseName);
            return true;
        }
        return false;
    }

    public boolean deleteDatabase(String databaseName){
        boolean isDatabaseDeleted = dao.deleteDatabase(databaseName);
        if(isDatabaseDeleted) {
            if(!databaseName.equals("db_system"))
                broadcast.broadcastDeleteDatabaseChange(databaseName);
            return true;
        }
        return false;
    }

    public String listDatabases(){
        List<String> listOfCollections = getListOfDatabasesFromDao();
        return formatListOfDatabasesForClient(listOfCollections);
    }

    private List<String> getListOfDatabasesFromDao(){
        return dao.listDatabases();
    }

    private String formatListOfDatabasesForClient(List<String> listOfDatabases){
        if(listOfDatabases == null)
            throw new IllegalArgumentException();
        StringBuilder stringBuilder = new StringBuilder("Databases:\n");
        int i = 1;
        for(String databaseName : listOfDatabases){
            if(databaseName.equals("db_system"))
                continue;
            stringBuilder.append(i).append("- ").append(databaseName).append("\n");
            i++;
        }
        return stringBuilder.toString();
    }
}
