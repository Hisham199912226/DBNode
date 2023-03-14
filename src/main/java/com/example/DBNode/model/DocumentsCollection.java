package com.example.DBNode.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class DocumentsCollection {
    private Map<String,Document> documents;
    private String collectionName;
    public DocumentsCollection(){
        this.documents = new HashMap<>();
    }
}
