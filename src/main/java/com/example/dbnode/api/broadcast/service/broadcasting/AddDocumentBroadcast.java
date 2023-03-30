package com.example.dbnode.api.broadcast.service.broadcasting;

import com.example.dbnode.utils.UrlBuilder;
import com.example.dbnode.api.broadcast.service.HttpService;
import com.example.dbnode.api.bootstrap.model.Node;
import com.example.dbnode.api.bootstrap.service.RetrieveClusterInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddDocumentBroadcast {
    private final RetrieveClusterInfoService clusterInfoService;
    private List<Node> clusterInfo = new ArrayList<>();
    private final HttpService httpService;

    public void broadcastAddDocumentChange(String databaseName, String collectionName, String jsonObject, String id){
        getClusterInfo();
        for(Node node : clusterInfo){
            httpService.postMethod(getBroadcastAddDocumentPath(databaseName,collectionName,node,id),jsonObject, String.class);
        }
    }

    private String getBroadcastAddDocumentPath(String databaseName, String collectionName, Node node, String id){
        String path = "/node/broadcast/document/add/" + databaseName + "/" +
                collectionName + "?id=" + id;
        return UrlBuilder.buildUrlString(node.getIpAddress(),node.getPort(),path);
    }

    private void getClusterInfo(){
        if(clusterInfo.isEmpty()) {
            clusterInfo = clusterInfoService.getClusterInfo();
        }
    }
}
