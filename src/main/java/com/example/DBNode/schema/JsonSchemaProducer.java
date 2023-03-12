package com.example.DBNode.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saasquatch.jsonschemainferrer.*;

public class JsonSchemaProducer {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final JsonSchemaInferrer jsonSchemaInferrer = JsonSchemaInferrer.newBuilder()
            .setSpecVersion(SpecVersion.DRAFT_06)
            .setAdditionalPropertiesPolicy(AdditionalPropertiesPolicies.notAllowed())
            .setRequiredPolicy(RequiredPolicies.nonNullCommonFields())
            .build();;

    public static String generateJsonSchema(String jsonSample) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(jsonSample);
        JsonNode schemaAsJsonNode = jsonSchemaInferrer.inferForSample(jsonNode);
        String schemaAsString = objectMapper.writeValueAsString(schemaAsJsonNode);
        return schemaAsString;
    }
}
