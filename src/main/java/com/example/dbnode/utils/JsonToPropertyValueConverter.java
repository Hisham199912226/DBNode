package com.example.dbnode.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonToPropertyValueConverter {

    private JsonToPropertyValueConverter(){

    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<String> convert(String jsonObject) throws JsonProcessingException {
        if (jsonObject == null)
            throw new IllegalArgumentException();
        JsonNode node = objectMapper.readTree(jsonObject);
        List<String> values = new ArrayList<>();
        traverseToConvert(node,"",values);
        return values;
    }

    private static void traverseToConvert(JsonNode node, String propertyName, List<String> values){
        if(node == null || propertyName == null || values == null)
            throw new IllegalArgumentException();
        if (node.isObject()) {
            for (Iterator<String> it = node.fieldNames(); it.hasNext(); ) {
                propertyName = it.next();
                JsonNode propertyNode = node.get(propertyName);
                if (propertyName.equals("_id"))
                    continue;
                traverseToConvert(propertyNode, propertyName, values);
            }
        } else if (node.isArray()) {
            for (JsonNode elementNode : node) {
                traverseToConvert(elementNode,propertyName,values);
            }
        } else {
            String[] splitValue = node.asText().split("\\s+");
            for (String value : splitValue) {
                value = formatValue(propertyName, value);
                values.add(value);
            }
        }
    }

    private static String formatValue(String propertyName, String value){
        if(propertyName == null || value == null)
            throw new IllegalArgumentException();
        StringBuilder sb = new StringBuilder();
        sb.append(propertyName);
        sb.append(":");
        sb.append(value.toLowerCase().intern());
        return sb.toString();
    }
}
