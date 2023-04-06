package com.example.dbnode.api.client.service;

import com.example.dbnode.api.broadcast.service.HttpService;
import com.example.dbnode.api.bootstrap.model.Node;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AffinityService {

    private final HttpService httpService;

    @Value("${bootstrap.node.host}")
    private String bootstrapNodeHost;

    @Value("${bootstrap.node.port}")
    private int bootstrapNodePort;

    public Node getWriteAffinity(){
        ResponseEntity<Node> responseMono = httpService.getMethod(String.format("http://%s:%d/bootstrap/getAffinity",bootstrapNodeHost,bootstrapNodePort), Node.class);
        return Objects.requireNonNull(responseMono.getBody());
    }
}
