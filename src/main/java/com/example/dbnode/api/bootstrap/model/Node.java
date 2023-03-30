package com.example.dbnode.api.bootstrap.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.net.*;

@Data
@NoArgsConstructor
@Component
public class Node implements Serializable {
    private String ipAddress;
    @Value("${node.name}")
    private String nodeName;
    @Value("${server.port}")
    private int port;


    @PostConstruct
    private void assignIpAddress() {
        try {
            this.ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unable to determine IP address", e);
        }
    }

}
