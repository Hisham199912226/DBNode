package com.example.dbnode.api.bootstrap.model;

import lombok.Data;

@Data
public class SysUser {

    private String username;
    private String password;
    private UserRole role;
}
