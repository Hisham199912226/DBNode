package com.example.dbnode.api.broadcast.controller.document;

import com.example.dbnode.api.broadcast.service.document.BroadcastDeleteDocumentService;
import com.example.dbnode.utils.ResponseEntityCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("node/broadcast/document/delete/many/{databaseName}/{collectionName}")
    public ResponseEntity<String> deleteManyDocuments(@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody List<String> ids) throws JsonProcessingException {
        boolean isDocumentsDeleted = broadcastDeleteDocumentService.deleteManyDocuments(databaseName,collectionName,ids);
        if(isDocumentsDeleted)
            return ResponseEntityCreator.getResponse(HttpStatus.OK,"Documents were successfully deleted!");
        return ResponseEntityCreator.getResponse(HttpStatus.BAD_REQUEST,"There is a problem with delete documents received from a broadcast message!");
    }
}
