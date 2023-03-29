package com.example.dbnode.api.broadcast.controller.document;

import com.example.dbnode.api.broadcast.service.document.BroadcastAddDocumentService;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class BroadcastAddDocumentController {

    private final BroadcastAddDocumentService broadcastAddDocumentService;

    @PostMapping("node/broadcast/document/add/{databaseName}/{collectionName}")
    public ResponseEntity<String> receiveAddDocumentChange(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String jsonObject, @RequestParam String id) throws IOException {
        boolean isDocumentCreated = broadcastAddDocumentService.addDocument(databaseName,collectionName,jsonObject,id);
        if(isDocumentCreated)
            return ResponseEntityCreator.getResponse(HttpStatus.CREATED,"Document was successfully created!");
        return ResponseEntityCreator.getResponse(HttpStatus.BAD_REQUEST,"There is a problem with adding document received from a broadcast message!");
    }
}
