package com.example.DBNode.api.controller.document;

import com.example.DBNode.api.service.PathValidationService;
import com.example.DBNode.api.service.document.*;
import com.example.DBNode.database.dao.DAO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UpdateDocumentController {
    private final DAO dao;
    private final UpdateDocumentService updateService;
    private final PathValidationService pathValidationService;






}
