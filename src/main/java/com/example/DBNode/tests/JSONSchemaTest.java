package com.example.DBNode.tests;


import com.example.DBNode.schema.*;
import java.io.IOException;
public class JSONSchemaTest {

    public static void main(String[] args) throws IOException {

        String jsonToGenerateSchema = "{ \"name\": \"John Doe\", \"age\": 42, \"email\": \"johndoe@example.com\", \"address\": { \"street\": \"123 Main St\", \"city\": \"Anytown\", \"state\": \"CA\", \"zip\": \"12345\" }, \"phoneNumbers\": [ { \"type\": \"home\", \"number\": \"555-1234\" }, { \"type\": \"work\", \"number\": \"555-5678\" } ] }";
        String jsonToValidate = "{ \"name\": \"John Doe\", \"age\": 42, \"email\": \"johndoe@example.com\", \"address\": { \"street\": \"123 Main St\", \"city\": \"Anytown\", \"state\": \"CA\", \"zip\": \"12345\" }, \"phoneNumbers\": [ { \"type\": \"home\", \"number\": \"555-1234\" }, { \"type\": \"work\", \"number\": \"555-5678\" } ] }";

        JsonSchemaProducer jsonSchemaProducer = new JsonSchemaProducer();
        String jsonSchema = jsonSchemaProducer.generateJsonSchema(jsonToGenerateSchema);
        System.out.println(JsonSchemaValidator.validateJsonObject(jsonSchema,jsonToValidate));  //true

        System.out.println();
        jsonToValidate = "{ \"name\": \"John Doe\", \"email\": \"johndoe@example.com\", \"address\": { \"street\": \"123 Main St\", \"city\": \"Anytown\", \"state\": \"CA\", \"zip\": \"12345\" }, \"phoneNumbers\": [ { \"type\": \"home\", \"number\": \"555-1234\" }, { \"type\": \"work\", \"number\": \"555-5678\" } ] }";
        jsonSchema = jsonSchemaProducer.generateJsonSchema(jsonToGenerateSchema);
        System.out.println(JsonSchemaValidator.validateJsonObject(jsonSchema,jsonToValidate));  //false
    }

}
