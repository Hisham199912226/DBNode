package com.example.dbnode.api.client.service.collection;

import com.example.dbnode.api.bootstrap.model.Node;
import com.example.dbnode.api.broadcast.service.broadcasting.CollectionBroadcast;
import com.example.dbnode.api.client.model.Document;
import com.example.dbnode.database.dao.DatabaseDAO;
import com.example.dbnode.utils.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionService {
    private final DatabaseDAO dao;
    private final CollectionBroadcast broadcast;
    private final Node node;

    public boolean createCollection(String databaseName, String collectionName,String jsonSchema) throws IOException {
        boolean isCollectionCreated =  dao.createCollection(databaseName,collectionName);
        Document document = DocumentMapper.jsonStringToDocument(jsonSchema);
        document.getDocument().put("version", 1);
        document.getDocument().put("owner", node);
        document.getDocument().put("_id", "");
        if(isCollectionCreated){
            dao.createSchemaFile(databaseName,collectionName,document.DocumentAsString());
            if(!databaseName.equals("db_system"))
                broadcast.broadcastCreateCollectionChange(databaseName,collectionName,document.DocumentAsString());
            return true;
        }
        return false;
    }

    public boolean deleteCollection(String databaseName, String collectionName){
        boolean isCollectionDeleted =  dao.deleteCollection(databaseName,collectionName);
        if(isCollectionDeleted){
            if(!databaseName.equals("db_system"))
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
