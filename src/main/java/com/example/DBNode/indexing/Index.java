package com.example.DBNode.indexing;

import com.example.DBNode.model.Document;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface Index {
    void buildIndex() throws JsonProcessingException;
    void addToIndex(Document document) throws JsonProcessingException;
    void removeFromIndex(Document document) throws JsonProcessingException;
    List<String> findBySingleValue(String value);
    List<String> findByMultiValues(List<String> values);
}
