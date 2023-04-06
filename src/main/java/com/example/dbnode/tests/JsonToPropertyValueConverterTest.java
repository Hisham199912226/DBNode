package com.example.dbnode.tests;

import com.example.dbnode.utils.JsonToPropertyValueConverter;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public class JsonToPropertyValueConverterTest {

    public static void main(String[] args) throws JsonProcessingException {
        String jsonObject = "{ \"name\": \"Alice\", \"age\": 30, \"isMarried\": false }";
        List<String> values = JsonToPropertyValueConverter.convert(jsonObject);
        System.out.println(values);
    }
}
