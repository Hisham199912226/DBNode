package com.example.dbnode.api.bootstrap.service;

import com.example.dbnode.api.bootstrap.model.Node;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetrieveClusterInfoService {
    @Value("${server.port}")
    private int port;
    @Value("${bootstrap.node.host}")
    private String bootstrapNodeHost;

    @Value("${bootstrap.node.port}")
    private int bootstrapNodePort;
    private final WebClient webClient;

    public List<Node> getClusterInfo(){
        Mono<ResponseEntity<List<Node>>> responseMono = webClient.get()
                .uri(String.format("http://%s:%d/bootstrap/clusterInfo",bootstrapNodeHost,bootstrapNodePort))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.empty())
                .toEntityList(Node.class);
        return Objects.requireNonNull(Objects.requireNonNull(responseMono.block()).getBody()).stream().filter(node -> node.getPort() != getNodeInfo().getPort()).collect(Collectors.toList());
    }

    private Node getNodeInfo(){
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
