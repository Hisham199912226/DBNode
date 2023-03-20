package com.example.dbnode.api.model;

import com.example.dbnode.indexing.TermIndex;
import lombok.Data;
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
