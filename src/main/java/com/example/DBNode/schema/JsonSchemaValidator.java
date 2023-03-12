package com.example.DBNode.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

public class JsonSchemaValidator {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static boolean validateJsonObject(String jsonSchema,String jsonObject){
        Schema schema = SchemaLoader.load(new JSONObject(jsonSchema));
        try {
            schema.validate(new JSONObject(jsonObject));
        } catch (ValidationException e){
            return false;
        }
        return true;
    }
}
