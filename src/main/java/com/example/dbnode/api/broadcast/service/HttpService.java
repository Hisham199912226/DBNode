package com.example.dbnode.api.broadcast.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class HttpService {
    private final WebClient webClient;

    public <T> ResponseEntity<T> getMethod(String uri, Class<T> responseType){
        return webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.empty())
                .toEntity(responseType).block();
    }

    public <T> ResponseEntity<T> postMethod(String uri, String bodyValue, Class<T> responseType){
        return webClient.post()
                .uri(uri)
                .bodyValue(bodyValue)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.empty())
                .toEntity(responseType).block();
    }

    public <T> ResponseEntity<T> deleteMethod(String uri, String bodyValue, Class<T> responseType){
        return webClient.method(HttpMethod.DELETE)
                .uri(uri)
                .bodyValue(bodyValue)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.empty())
                .toEntity(responseType).block();
    }
    public <T> ResponseEntity<T> putMethod(String uri,String bodyValue, Class<T> responseType){
        return webClient.put()
                .uri(uri)
                .bodyValue(bodyValue)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.empty())
                .toEntity(responseType).block();
    }


}
