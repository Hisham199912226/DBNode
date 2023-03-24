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
public class DatabaseBroadcast {

    private final RetrieveClusterInfoService clusterInfoService;
    private List<Node> clusterInfo = new ArrayList<>();
    private final WebClient webClient;

    public void broadcastCreateDatabaseChange(String databaseName){
        getClusterInfo();
        System.out.println(clusterInfo);
        for(Node node : clusterInfo){
            Mono<ResponseEntity<String>> response = webClient.post()
                    .uri(getBroadcastCreateDatabasePath(databaseName,node))
                    .retrieve()
                    .toEntity(String.class);
            response.block();
        }
    }

    private String getBroadcastCreateDatabasePath(String databaseName, Node node){
        return "http://" + node.getIpAddress() + ":" +
                node.getPort() +
                "/node/broadcast/database/create/" +
                databaseName;
    }


    public void broadcastDeleteDatabaseChange(String databaseName){
        getClusterInfo();
        System.out.println(clusterInfo);
        for(Node node : clusterInfo){
            Mono<ResponseEntity<String>> response = webClient.post()
                    .uri(getBroadcastDeleteDatabasePath(databaseName,node))
                    .retrieve()
                    .toEntity(String.class);
            response.block();
        }
    }

    private String getBroadcastDeleteDatabasePath(String databaseName, Node node){
        return "http://" + node.getIpAddress() + ":" +
                node.getPort() +
                "/node/broadcast/database/delete/" +
                databaseName;
    }


    private void getClusterInfo(){
        if(clusterInfo.isEmpty()) {
            clusterInfo = clusterInfoService.getClusterInfo();
        }
    }

}
