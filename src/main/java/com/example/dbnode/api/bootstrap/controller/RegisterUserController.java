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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class RegisterUserController {
    private final AddDocumentService addDocumentService;

    private final PasswordEncoder encoder;

    @PostMapping("node/bootstrap/register/user")
    public ResponseEntity<String> registerUser(@RequestBody String jsonUser) throws IOException {
        jsonUser = getJsonUserWithEncodedPassword(jsonUser);
        System.out.println(jsonUser);
        addDocumentService.generateIdAndAddDocument("users", "users", jsonUser);
        return ResponseEntityCreator.getResponse(HttpStatus.OK, "User Registered Successfully");
    }

    private String getJsonUserWithEncodedPassword(String jsonUser) throws JsonProcessingException{
        ObjectNode objectNode = new ObjectMapper().readValue(jsonUser, ObjectNode.class);
        JsonNode passwordNode = objectNode.get("password");
        String encodedPassword = encodePassword(passwordNode);
        System.out.println("encodedPassword : "  + encodedPassword);
        objectNode.put("password",encodedPassword);
        System.out.println("ObjectNode " + objectNode);
        return objectNode.toString();
    }
    private String encodePassword(JsonNode passwordNode)  {
        String password = passwordNode.toString();
        password = password.replace("\"","");
        System.out.println(password);
        System.out.println(encoder.encode(password));
        return encoder.encode(password);
    }


}
