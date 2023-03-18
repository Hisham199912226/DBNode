package com.example.DBNode.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateOneRequestBody {
    private JsonNode jsonObject;
    private JsonNode newContent;
}
