package com.example.dbnode.api.client.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

@Data
public class Document {
    private ConcurrentHashMap<String,Object> document;

    private String id;
    public Document() {
        this.document = new ConcurrentHashMap<>();
        this.id = "";
    }

    public Document getReference(){
        return this;
    }

    public String DocumentAsString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this.document);
    }
}
