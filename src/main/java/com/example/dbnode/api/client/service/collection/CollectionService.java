package com.example.dbnode.api.client.service.collection;

import com.example.dbnode.api.broadcast.service.broadcasting.CollectionBroadcast;
import com.example.dbnode.database.dao.DatabaseDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionService {
    private final DatabaseDAO dao;

    private final CollectionBroadcast broadcast;

    public boolean createCollection(String databaseName, String collectionName){
        boolean isCollectionCreated =  dao.createCollection(databaseName,collectionName);
        if(isCollectionCreated){
            broadcast.broadcastCreateCollectionChange(databaseName,collectionName);
            return true;
        }
        return false;
    }

    public boolean deleteCollection(String databaseName, String collectionName){
        boolean isCollectionDeleted =  dao.deleteCollection(databaseName,collectionName);
        if(isCollectionDeleted){
            broadcast.broadcastDeleteCollectionChange(databaseName,collectionName);
            return true;
        }
        return false;
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
