package com.example.dbnode.api.bootstrap.service;


import com.example.dbnode.api.bootstrap.model.Node;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
@RequiredArgsConstructor
public class NodeRegistrationService {

    private final WebClient webClient;
    @Value("${server.port}")
    private int port;

    @Value("${bootstrap.node.host}")
    private String bootstrapNodeHost;

    @Value("${bootstrap.node.port}")
    private int bootstrapNodePort;

    @PostConstruct
    public void registerNode() {
        Node node = prepareNodeInfo();

        String response = webClient.post()
                .uri(String.format("http://%s:%d/bootstrap/register/node", bootstrapNodeHost, bootstrapNodePort))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(node)
                .retrieve()
                .bodyToMono(String.class).block();

        System.out.println(response);
    }

    private Node prepareNodeInfo(){
        Node node = new Node();
        node.setIpAddress(getIpAddress());
        node.setPort(port);

        return node;
    }

    private String getIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unable to determine IP address", e);
        }
    }
}
