package com.example.dbnode.api.client.service;

import com.example.dbnode.api.client.service.collection.CollectionService;
import com.example.dbnode.api.client.service.database.DatabaseService;
import com.example.dbnode.api.client.service.document.AddDocumentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class DefaultAdminService {

    private final DatabaseService databaseService;
    private final CollectionService collectionService;
    private final AddDocumentService addDocumentService;
    private final PasswordEncoder encoder;
    private final static ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void prepareDefaultDatabaseWithDefaultAdmin() throws IOException {
        String jsonUser = "{\"username\":\"admin\", \"password\":\"admin\", \"role\": \"ADMIN\" }";
        if(databaseService.createDatabase("db_system")){
            collectionService.createCollection("db_system","users");
            jsonUser = getJsonUserWithEncodedPassword(jsonUser);
            addDocumentService.generateIdAndAddDocument("db_system","users",jsonUser);
        }
    }

    private String getJsonUserWithEncodedPassword(String jsonUser) throws JsonProcessingException {
        ObjectNode objectNode = mapper.readValue(jsonUser, ObjectNode.class);
        JsonNode passwordNode = objectNode.get("password");
        String encodedPassword = encodePassword(passwordNode);
        objectNode.put("password",encodedPassword);
        return objectNode.toString();
    }
    private String encodePassword(JsonNode passwordNode)  {
        String password = passwordNode.toString();
        password = password.replace("\"","");
        return encoder.encode(password);
    }
}
