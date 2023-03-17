package com.example.DBNode.api.service.document;

import com.example.DBNode.database.dao.DAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateDocumentService {

    private final DAO dao;

    public boolean updateDocument(){
        return false;
    }
}
