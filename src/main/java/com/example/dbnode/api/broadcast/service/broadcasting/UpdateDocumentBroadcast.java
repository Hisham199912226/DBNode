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
public class UpdateDocumentBroadcast {
    private final RetrieveClusterInfoService clusterInfoService;
    private List<Node> clusterInfo = new ArrayList<>();
    private final HttpService httpService;

    public void broadcastUpdateDocumentChange(String databaseName, String collectionName, String newContent, String id){
        getClusterInfo();
        for(Node node : clusterInfo){
            httpService.putMethod(getBroadcastUpdateDocumentPath(databaseName,collectionName,node,id),newContent, String.class);
        }
    }

    private String getBroadcastUpdateDocumentPath(String databaseName, String collectionName, Node node, String id){
        if(databaseName == null || collectionName == null || node == null || id == null)
            throw new IllegalArgumentException();
        String path = "/node/broadcast/document/update/one/" +
                databaseName + "/" + collectionName + "?id=" + id;
        return UrlBuilder.buildUrlString(node.getIpAddress(),node.getPort(),path);
    }

    private void getClusterInfo(){
        if(clusterInfo.isEmpty()) {
            clusterInfo = clusterInfoService.getClusterInfo();
        }
    }

}
