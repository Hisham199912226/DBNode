package com.example.dbnode.api.client.service;

import com.example.dbnode.authentication.users.CustomUserDetails;
import com.example.dbnode.authentication.users.UserJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserJwtService userJwtService;

    public String authenticateUser(CustomUserDetails userDetails){
        if(userDetails == null)
            throw new IllegalArgumentException();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetails.getUsername(),userDetails.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
        return userJwtService.generateToken(userDetails);
    }
}
