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
public class DeleteDocumentBroadcast {

    private final RetrieveClusterInfoService clusterInfoService;
    private List<Node> clusterInfo = new ArrayList<>();
    private final HttpService httpService;

    public void broadcastDeleteDocumentChange(String databaseName, String collectionName, String id){
        getClusterInfo();
        for(Node node : clusterInfo){
            httpService.deleteMethod(getBroadcastDeleteDocumentPath(databaseName,collectionName,node,id),"", String.class);
        }
    }

    private String getBroadcastDeleteDocumentPath(String databaseName, String collectionName, Node node, String id){
        if(databaseName == null || collectionName == null || node == null || id == null)
            throw new IllegalArgumentException();
        String path = "/node/broadcast/document/delete/one/" +
                databaseName + "/" + collectionName + "?id=" + id;
        return UrlBuilder.buildUrlString(node.getIpAddress(),node.getPort(),path);
    }



    private void getClusterInfo(){
        if(clusterInfo.isEmpty()) {
            clusterInfo = clusterInfoService.getClusterInfo();
        }
    }
}
