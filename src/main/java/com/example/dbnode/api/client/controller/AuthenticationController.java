package com.example.dbnode.api.client.controller;

import com.example.dbnode.api.client.service.AuthenticationService;
import com.example.dbnode.authentication.users.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    @PostMapping("node/authenticate")
    public String authenticateUser(@RequestBody CustomUserDetails user){
        return authenticationService.authenticateUser(user);
    }
}
