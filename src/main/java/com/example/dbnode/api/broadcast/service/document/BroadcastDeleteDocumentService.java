package com.example.dbnode.api.broadcast.service.document;

import com.example.dbnode.api.client.service.document.DeleteDocumentService;
import com.example.dbnode.database.dao.DAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BroadcastDeleteDocumentService {

    private final DAO dao;

    private final DeleteDocumentService deleteDocumentService;

    public boolean deleteOneDocument(String databaseName, String collectionName, String id) throws JsonProcessingException {
        return deleteDocumentService.deleteDocumentByID(databaseName,collectionName,id);
    }

    public boolean deleteManyDocuments(String databaseName, String collectionName, List<String> ids){
        return false;
    }


}
