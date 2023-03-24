package com.example.dbnode.api.bootstrap.service;

import com.example.dbnode.api.bootstrap.model.Node;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AffinityService {

    private final WebClient webClient;

    public Node getWriteAffinity(){
        Mono<ResponseEntity<Node>> responseMono = webClient.get()
                .uri(String.format("http://%s:%d/bootstrap/getAffinity","localhost",8081))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.empty())
                .toEntity(Node.class);

        return Objects.requireNonNull(responseMono.block()).getBody();
    }
}
