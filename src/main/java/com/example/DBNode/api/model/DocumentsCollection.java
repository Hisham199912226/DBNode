package com.example.DBNode.api.model;

import com.example.DBNode.indexing.TermIndex;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class DocumentsCollection {
    private ConcurrentHashMap<String,Document> documents;
    private String collectionName;
    private TermIndex index;
    public DocumentsCollection(){
        this.documents = new ConcurrentHashMap<>();
        this.index = new TermIndex( this.documents);
    }
}
