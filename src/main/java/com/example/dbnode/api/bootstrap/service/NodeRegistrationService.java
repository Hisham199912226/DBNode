package com.example.dbnode.api.bootstrap.service;


import com.example.dbnode.api.bootstrap.model.Node;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class NodeRegistrationService {

    private final Node node;
    private final WebClient webClient;

    @Value("${bootstrap.node.host}")
    private String bootstrapNodeHost;

    @Value("${bootstrap.node.port}")
    private int bootstrapNodePort;

    @PostConstruct
    public void registerNode() {

        String response = webClient.post()
                .uri(String.format("http://%s:%d/bootstrap/register/node", bootstrapNodeHost, bootstrapNodePort))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(node)
                .retrieve()
                .bodyToMono(String.class).block();

        System.out.println(response);
    }

}
