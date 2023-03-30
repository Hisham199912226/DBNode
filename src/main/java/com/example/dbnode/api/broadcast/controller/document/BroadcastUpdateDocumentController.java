package com.example.dbnode.api.broadcast.controller.document;

import com.example.dbnode.api.broadcast.service.document.BroadcastUpdateDocumentService;
import com.example.dbnode.utils.ResponseEntityCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class BroadcastUpdateDocumentController {

    private final BroadcastUpdateDocumentService broadcastUpdateDocumentService;

    @PutMapping("node/broadcast/document/update/one/{databaseName}/{collectionName}")
    public ResponseEntity<String> updateDocument(@PathVariable String collectionName, @PathVariable String databaseName, @RequestBody String newContent, @RequestParam String id) throws IOException {
        boolean isDocumentUpdated = broadcastUpdateDocumentService.broadcastUpdateDocumentChange(databaseName,collectionName,newContent,id);
        if(isDocumentUpdated)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Document was successfully updated!");
        return ResponseEntityCreator.getResponse(HttpStatus.BAD_REQUEST,"There is a problem with update document received from a broadcast message!");
    }

}
