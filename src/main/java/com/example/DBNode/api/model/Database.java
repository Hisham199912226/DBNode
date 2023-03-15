package com.example.DBNode.api.model;


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Database {
    private Map<String,DocumentsCollection> collections;

    public Database(){
        this.collections = new HashMap<>();
    }
}
