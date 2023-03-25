package com.example.dbnode.api.broadcast.service.broadcasting;


import com.example.dbnode.api.bootstrap.model.Node;
import com.example.dbnode.api.bootstrap.service.RetrieveClusterInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateDocumentBroadcast {
    private final RetrieveClusterInfoService clusterInfoService;
    private List<Node> clusterInfo = new ArrayList<>();
    private final WebClient webClient;

    public void broadcastUpdateDocumentChange(String databaseName, String collectionName, String newContent, String id){
        getClusterInfo();
       // System.out.println(clusterInfo);
        for(Node node : clusterInfo){
            Mono<ResponseEntity<String>> response = webClient.post()
                    .uri(getBroadcastDeleteDocumentPath(databaseName,collectionName,node,id))
                    .bodyValue(newContent)
                    .retrieve()
                    .toEntity(String.class);
            response.block();
        }
    }

    private String getBroadcastDeleteDocumentPath(String databaseName, String collectionName, Node node, String id){
        return "http://" + node.getIpAddress() + ":" +
                node.getPort() +
                "/node/broadcast/document/update/one/" +
                databaseName + "/" + collectionName + "?id=" + id;
    }

    private void getClusterInfo(){
        if(clusterInfo.isEmpty()) {
            clusterInfo = clusterInfoService.getClusterInfo();
        }
    }

}
