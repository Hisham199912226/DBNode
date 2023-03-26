package com.example.dbnode.authentication.users;

import com.example.dbnode.api.client.service.document.ReadDocumentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ReadDocumentService readDocumentService;



    @Autowired(required = false)
    public CustomUserDetailsService(){

    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String searchValue;
        try {
            searchValue = prepareSearchValue(username);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(searchValue);
        String userAsJsonString = "";
        try {
            Optional<String> res = readDocumentService.readDocument("users","users",searchValue);
            if(res.isPresent())
                userAsJsonString = res.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(userAsJsonString.equals(""));
        if(userAsJsonString.equals("")) {
            throw new UsernameNotFoundException("user not found");
        }
        CustomUserDetails customUserDetails;
        try {
            ObjectNode objectNode = objectMapper.readValue(userAsJsonString, ObjectNode.class);
            objectNode.remove("_id");
            userAsJsonString = objectNode.toString();
            customUserDetails = objectMapper.readValue(userAsJsonString, CustomUserDetails.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return customUserDetails;

    }

    private String prepareSearchValue(String username) throws JsonProcessingException {
        if(username == null)
            throw new IllegalArgumentException();
        ObjectNode usernameAsJson = objectMapper.createObjectNode();
        usernameAsJson.put("username", username);
        return usernameAsJson.toString();
    }
}
