package com.example.DBNode.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Document {
    private Map<String,Object> document;

    private String id;
    public Document() {
        this.document = new HashMap<>();
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
