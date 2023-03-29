package com.example.dbnode.api.broadcast.service.broadcasting;

import com.example.dbnode.api.bootstrap.model.Node;
import com.example.dbnode.api.bootstrap.service.RetrieveClusterInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionBroadcast {
    private final RetrieveClusterInfoService clusterInfoService;
    private List<Node> clusterInfo = new ArrayList<>();
    private final WebClient webClient;

    public void broadcastCreateCollectionChange(String databaseName, String collectionName){
        getClusterInfo();
        System.out.println(clusterInfo);
        for(Node node : clusterInfo){
            Mono<ResponseEntity<String>> response = webClient.post()
                    .uri(getBroadcastCreateCollectionPath(databaseName,collectionName,node))
                    .retrieve()
                    .toEntity(String.class);
            response.block();
        }
    }

    private String getBroadcastCreateCollectionPath(String databaseName, String collectionName, Node node){
        return "http://" + node.getIpAddress() + ":" +
                node.getPort() +
                "/node/broadcast/collection/create/" +
                databaseName + "/" + collectionName;
    }


    public void broadcastDeleteCollectionChange(String databaseName, String collectionName){
        getClusterInfo();
        for(Node node : clusterInfo){
            Mono<ResponseEntity<String>> response = webClient.post()
                    .uri(getBroadcastDeleteCollectionPath(databaseName,collectionName,node))
                    .retrieve()
                    .toEntity(String.class);
            response.block();
        }
    }

    private String getBroadcastDeleteCollectionPath(String databaseName, String collectionName, Node node){
        return "http://" + node.getIpAddress() + ":" +
                node.getPort() +
                "/node/broadcast/collection/delete/" +
                databaseName+ "/" + collectionName;
    }


    private void getClusterInfo(){
        if(clusterInfo.isEmpty()) {
            clusterInfo = clusterInfoService.getClusterInfo();
        }
    }
}
