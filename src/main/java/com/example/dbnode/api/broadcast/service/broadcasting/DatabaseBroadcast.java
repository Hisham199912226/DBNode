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
public class DatabaseBroadcast {

    private final RetrieveClusterInfoService clusterInfoService;
    private List<Node> clusterInfo = new ArrayList<>();
    private final HttpService httpService;

    public void broadcastCreateDatabaseChange(String databaseName){
        getClusterInfo();
        for(Node node : clusterInfo){
            httpService.postMethod(getBroadcastCreateDatabasePath(databaseName,node),"", String.class);
        }
    }

    private String getBroadcastCreateDatabasePath(String databaseName, Node node){
        String path = "/node/broadcast/database/create/" +
                databaseName;
        return UrlBuilder.buildUrlString(node.getIpAddress(),node.getPort(),path);
    }


    public void broadcastDeleteDatabaseChange(String databaseName){
        getClusterInfo();
        for(Node node : clusterInfo){
            httpService.postMethod(getBroadcastDeleteDatabasePath(databaseName,node),"", String.class);
        }
    }

    private String getBroadcastDeleteDatabasePath(String databaseName, Node node){
        String path = "/node/broadcast/database/delete/" +
                databaseName;
        return UrlBuilder.buildUrlString(node.getIpAddress(),node.getPort(),path);
    }


    private void getClusterInfo(){
        if(clusterInfo.isEmpty()) {
            clusterInfo = clusterInfoService.getClusterInfo();
        }
    }

}
