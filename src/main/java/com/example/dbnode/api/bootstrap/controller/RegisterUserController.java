package com.example.dbnode.api.bootstrap.controller;

import com.example.dbnode.api.client.service.document.AddDocumentService;
import com.example.dbnode.api.client.service.document.ReadDocumentService;
import com.example.dbnode.utils.ResponseEntityCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class RegisterUserController {
    private final AddDocumentService addDocumentService;
    private final ReadDocumentService readDocumentService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("node/register/user")
    public ResponseEntity<String> registerUser(@RequestBody String jsonUser) throws IOException {
        String searchValue = prepareSearchValue(jsonUser);
        if(isUserAlreadyRegistered(searchValue)) {
            return ResponseEntityCreator.getResponse(HttpStatus.CONFLICT, "User with this username is already registered");
        }
        System.out.println(jsonUser);
        addDocumentService.generateIdAndAddDocument("users", "users", jsonUser);
        return ResponseEntityCreator.getResponse(HttpStatus.OK, "User Registered Successfully");
    }

    private boolean isUserAlreadyRegistered(String searchValue) throws IOException {
        Optional<String> result = readDocumentService.readDocument("users","users",searchValue);
        return result.isPresent();
    }

    private String prepareSearchValue(String jsonUser) throws JsonProcessingException {
        if(jsonUser == null)
            throw new IllegalArgumentException();
        JsonNode rootNode = objectMapper.readTree(jsonUser);
        JsonNode usernameNode = rootNode.get("username");
        ObjectNode usernameAsJson = objectMapper.createObjectNode();
        usernameAsJson.set("username", usernameNode);
        return usernameAsJson.toString();
    }
}