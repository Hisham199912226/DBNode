package com.example.dbnode.api.broadcast.controller.document;

import com.example.dbnode.api.broadcast.service.document.BroadcastDeleteDocumentService;
import com.example.dbnode.utils.ResponseEntityCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BroadcastDeleteDocumentController {

    private final BroadcastDeleteDocumentService broadcastDeleteDocumentService;

    @PostMapping("node/broadcast/document/delete/one/{databaseName}/{collectionName}")
    public ResponseEntity<String> deleteOneDocument(@PathVariable String databaseName, @PathVariable String collectionName, @RequestParam String id) throws JsonProcessingException {
        boolean isDocumentDeleted = broadcastDeleteDocumentService.deleteOneDocument(databaseName,collectionName,id);
        if(isDocumentDeleted)
            return ResponseEntityCreator.getResponse(HttpStatus.CREATED,"Document was successfully deleted!");
        return ResponseEntityCreator.getResponse(HttpStatus.BAD_REQUEST,"There is a problem with delete document received from a broadcast message!");
    }
}
