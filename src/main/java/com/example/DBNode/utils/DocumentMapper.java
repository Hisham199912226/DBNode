package com.example.DBNode.utils;

import com.example.DBNode.model.Document;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.HashMap;


public class DocumentMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Document FileToDocument(File file) throws IOException {
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
        HashMap<String,Object> json = objectMapper.readValue(jsonString.toString(),HashMap.class);
        document.setDocument(json);
        return document;
    }

    public static Document jsonStringToDocument(String jsonString) throws IOException {
        if(jsonString == null)
            throw new IllegalArgumentException();
        Document document = new Document();
        HashMap<String,Object> json = objectMapper.readValue(jsonString,HashMap.class);
        document.setDocument(json);
        return document;
    }

    public static String DocumentToJsonString(Document document) throws JsonProcessingException {
        if(document == null)
            throw new IllegalArgumentException();
        return objectMapper.writeValueAsString(document.getDocument());
    }
}
