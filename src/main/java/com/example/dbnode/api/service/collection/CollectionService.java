package com.example.dbnode.api.service.collection;

import com.example.dbnode.database.dao.DatabaseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public String listCollections(String databaseName){
        List<String> listOfCollections = getListOfCollectionsFromDao(databaseName);
        return formatListOfCollectionsForClient(databaseName,listOfCollections);
    }

    private List<String> getListOfCollectionsFromDao(String databaseName){
        return dao.listCollections(databaseName);
    }

    private String formatListOfCollectionsForClient(String databaseName,List<String> listOfCollections){
        if(databaseName == null || listOfCollections == null)
            throw new IllegalArgumentException();
        StringBuilder stringBuilder = new StringBuilder(databaseName + " Collections:\n");
        int i = 1;
        for(String collectionName : listOfCollections){
            stringBuilder.append(i).append("- ").append(collectionName).append("\n");
            i++;
        }
        return stringBuilder.toString();
    }

    public boolean checkIfDatabaseExist(String databaseName){
        long count = dao.listDatabases().stream().filter(dbName -> dbName.equals(databaseName)).count();
        return (count == 1);
    }
}
