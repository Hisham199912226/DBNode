package com.example.dbnode.api.client.controller.database;

import com.example.dbnode.api.client.service.database.DatabaseService;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class DatabaseController {
    private final DatabaseService databaseService;
    @PostMapping("node/client/create/database/{databaseName}")
    public ResponseEntity<String> createDatabase(@PathVariable("databaseName") String databaseName, Principal principal){
        if(databaseName.equals("db_system"))
            return ResponseEntityCreator.getResponse(HttpStatus.FORBIDDEN,"This database name reserved for system");
        if(hasRole(principal,"USER"))
            return ResponseEntityCreator.getResponse(HttpStatus.FORBIDDEN,"You can not create database as a user! you need the admin role to have this privilege ");
        boolean isDatabaseCreated = databaseService.createDatabase(databaseName);
        if(isDatabaseCreated)
            return ResponseEntityCreator.getResponse(HttpStatus.CREATED,"Database was created successfully!");
        else
            return ResponseEntityCreator.getResponse(HttpStatus.CONFLICT,"Database you tried to create is already exist");
    }

    @DeleteMapping("node/client/delete/database/{databaseName}")
    public ResponseEntity<String> deleteDatabase(@PathVariable("databaseName") String databaseName, Principal principal){
        if(databaseName.equals("db_system"))
            return ResponseEntityCreator.getResponse(HttpStatus.FORBIDDEN,"db_system can not be deleted!");
        if(hasRole(principal,"USER"))
            return ResponseEntityCreator.getResponse(HttpStatus.FORBIDDEN,"You can not delete database as a user! you need the admin role to have this privilege ");
        boolean isDatabaseDeleted = databaseService.deleteDatabase(databaseName);
        if(isDatabaseDeleted)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Database was deleted successfully!");
        else
            return ResponseEntityCreator.getResponse(HttpStatus.NOT_FOUND,"Database you tried to delete does not exist");
    }

    @GetMapping("node/client/list/databases")
    public ResponseEntity<String> listDatabases(){
        return ResponseEntityCreator.getResponse(HttpStatus.OK,databaseService.listDatabases());
    }

    private boolean hasRole(Principal principal, String roleName) {
        if (principal instanceof Authentication) {
            Authentication authentication = (Authentication) principal;
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(roleName));
        }
        return false;
    }

}
