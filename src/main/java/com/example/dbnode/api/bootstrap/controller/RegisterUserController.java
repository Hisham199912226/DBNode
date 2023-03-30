package com.example.dbnode.api.bootstrap.controller;

import com.example.dbnode.api.client.service.document.AddDocumentService;
import com.example.dbnode.utils.ResponseEntityCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class RegisterUserController {
    private final AddDocumentService addDocumentService;
    private final PasswordEncoder encoder;
    private final static  ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("node/bootstrap/register/user")
    public ResponseEntity<String> registerUser(@RequestBody String jsonUser) throws IOException {
        jsonUser = getJsonUserWithEncodedPassword(jsonUser);
        addDocumentService.generateIdAndAddDocument("db_system", "users", jsonUser);
        return ResponseEntityCreator.getResponse(HttpStatus.OK, "User Registered Successfully");
    }

    private String getJsonUserWithEncodedPassword(String jsonUser) throws JsonProcessingException{
        ObjectNode objectNode = objectMapper.readValue(jsonUser, ObjectNode.class);
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
