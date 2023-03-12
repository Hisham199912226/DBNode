package com.example.DBNode.model;

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
    }

    public String DocumentAsString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this.document);
    }
}
