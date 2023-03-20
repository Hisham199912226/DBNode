package com.example.dbnode.api.service.database;

import com.example.dbnode.database.dao.DatabaseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
            stringBuilder.append(i).append("- ").append(databaseName).append("\n");
            i++;
        }
        return stringBuilder.toString();
    }
}
