package com.example.dbnode.schema;


import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

public class JsonSchemaValidator {

    private JsonSchemaValidator(){

    }

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
