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

    @PostMapping("node/bootstrap/register/user")
    public ResponseEntity<String> registerUser(@RequestBody String jsonUser) throws IOException {
        addDocumentService.generateIdAndAddDocument("users", "users", jsonUser);
        return ResponseEntityCreator.getResponse(HttpStatus.OK, "User Registered Successfully");
    }


}
