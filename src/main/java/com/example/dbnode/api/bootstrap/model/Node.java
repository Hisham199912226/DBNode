package com.example.dbnode.api.bootstrap.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Node implements Serializable {
    private String ipAddress;
    private int port;

    public Node(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }
}
