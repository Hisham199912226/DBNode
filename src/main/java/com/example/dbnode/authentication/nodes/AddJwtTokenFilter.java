package com.example.dbnode.authentication.nodes;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class AddJwtTokenFilter implements ExchangeFilterFunction {

    private final NodesJwtService nodesJwtService;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        String jwtToken = nodesJwtService.generateToken("node");
        ClientRequest authorizedRequest = ClientRequest.from(request)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .build();
        return next.exchange(authorizedRequest);
    }
}
