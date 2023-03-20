package com.example.dbnode.utils;

import com.example.dbnode.api.model.Document;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;


public class DocumentMapper {

    private DocumentMapper(){

    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Document fileToDocument(File file) throws IOException {
        if(file == null)
            throw new IllegalArgumentException();
        Document document = new Document();
        StringBuilder jsonString;
        if(!file.exists())
            return document;
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){
            String line;
            jsonString = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                jsonString.append(line);
            }
        }
        ConcurrentHashMap<String,Object> json = objectMapper.readValue(jsonString.toString(), ConcurrentHashMap.class);
        document.setDocument(json);
        return document;
    }

    public static Document jsonStringToDocument(String jsonString) throws IOException {
        if(jsonString == null)
            throw new IllegalArgumentException();
        Document document = new Document();
        ConcurrentHashMap<String,Object> json = objectMapper.readValue(jsonString, ConcurrentHashMap.class);
        document.setDocument(json);
        return document;
    }

    public static String documentToJsonString(Document document) throws JsonProcessingException {
        if(document == null)
            throw new IllegalArgumentException();
        return objectMapper.writeValueAsString(document.getDocument());
    }
}
