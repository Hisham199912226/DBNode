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
public class AddDocumentBroadcast {
    private final RetrieveClusterInfoService clusterInfoService;
    private List<Node> clusterInfo = new ArrayList<>();
    private final WebClient webClient;

    public void broadcastAddDocumentChange(String databaseName, String collectionName, String jsonObject, String id){
        getClusterInfo();
        System.out.println(clusterInfo);
        for(Node node : clusterInfo){
            Mono<ResponseEntity<String>> response = webClient.post()
                    .uri(getBroadcastAddDocumentPath(databaseName,collectionName,node,id))
                    .bodyValue(jsonObject)
                    .retrieve()
                    .toEntity(String.class);
            response.block();
        }
    }

    private String getBroadcastAddDocumentPath(String databaseName, String collectionName, Node node, String id){
        return "http://" + node.getIpAddress() + ":" +
                node.getPort() +
                "/node/broadcast/document/add/" +
                databaseName + "/" + collectionName + "?id=" + id;
    }

    private void getClusterInfo(){
        if(clusterInfo.isEmpty()) {
            clusterInfo = clusterInfoService.getClusterInfo();
        }
    }
}
