package com.example.dbnode.api.broadcast.service.broadcasting;

import com.example.dbnode.utils.UrlBuilder;
import com.example.dbnode.api.broadcast.service.HttpService;
import com.example.dbnode.api.bootstrap.model.Node;
import com.example.dbnode.api.bootstrap.service.RetrieveClusterInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionBroadcast {
    private final RetrieveClusterInfoService clusterInfoService;
    private List<Node> clusterInfo = new ArrayList<>();
    private final WebClient webClient;
    private final HttpService httpService;

    public void broadcastCreateCollectionChange(String databaseName, String collectionName){
        getClusterInfo();
        System.out.println(clusterInfo);
        for(Node node : clusterInfo){
            httpService.postMethod(getBroadcastCreateCollectionPath(databaseName,collectionName,node),"", String.class);
        }
    }

    private String getBroadcastCreateCollectionPath(String databaseName, String collectionName, Node node){
        String path = "/node/broadcast/collection/create/" +
                databaseName + "/" + collectionName;
        return UrlBuilder.buildUrlString(node.getIpAddress(),node.getPort(),path);
    }


    public void broadcastDeleteCollectionChange(String databaseName, String collectionName){
        getClusterInfo();
        for(Node node : clusterInfo){
            httpService.postMethod(getBroadcastDeleteCollectionPath(databaseName,collectionName,node),"", String.class);
        }
    }

    private String getBroadcastDeleteCollectionPath(String databaseName, String collectionName, Node node){
        String path = "/node/broadcast/collection/delete/" +
                databaseName+ "/" + collectionName;
        return UrlBuilder.buildUrlString(node.getIpAddress(),node.getPort(),path);
    }


    private void getClusterInfo(){
        if(clusterInfo.isEmpty()) {
            clusterInfo = clusterInfoService.getClusterInfo();
        }
    }
}
