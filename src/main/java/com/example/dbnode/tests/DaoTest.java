package com.example.dbnode.tests;

import com.example.dbnode.database.dao.DAO;
import com.example.dbnode.database.dao.DatabaseDAO;
import com.example.dbnode.api.model.Document;
import com.example.dbnode.api.model.DocumentsCollection;
import com.example.dbnode.utils.DocumentMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DaoTest {
    public static void main(String[] args) throws IOException {
        DAO dao = new DatabaseDAO();

        //Create Databases
        dao.createDatabase("db1");
        dao.createDatabase("db2");
        dao.createDatabase("db3");

        //Create Collections
        dao.createCollection("db1","col1");
        dao.createCollection("db1","col2");
        dao.createCollection("db1","col3");

        dao.createCollection("db2","col1");
        dao.createCollection("db2","col2");

        dao.createCollection("db3","col1");

        //List Databases
        System.out.println("Databases :");
        System.out.println(dao.listDatabases());
        System.out.println("=====================================================");

        //List Collections at db1
        System.out.println("Collections at db1 :");
        System.out.println(dao.listCollections("db1"));
        System.out.println("=====================================================");

        //List Collections at db2
        System.out.println("Collections at db2 :");
        System.out.println(dao.listCollections("db2"));
        System.out.println("=====================================================");

        //List Collections at db3
        System.out.println("Collections at db3 :");
        System.out.println(dao.listCollections("db3"));

        //Add Documents to col1 at db1
        String jsonString = "{\"name\" : \"hisham\", \"age\" : 22}";
        dao.addDocument("db1","col1", DocumentMapper.jsonStringToDocument(jsonString));
        dao.addDocument("db1","col1", DocumentMapper.jsonStringToDocument(jsonString));
        dao.addDocument("db1","col1", DocumentMapper.jsonStringToDocument(jsonString));
        System.out.println("=====================================================");

        //Get number of documents at db1 at col1
        System.out.print("we have ");
        System.out.print(dao.countDocuments("db1","col1"));
        System.out.print(" documents at db1 col1 ");
        System.out.println();
        System.out.println("=====================================================");

        //Get col1 as DocumentsCollection object which has a hash map <String id, Document>
        DocumentsCollection documents = dao.readCollection("db1","col1");
        System.out.println(documents);
        System.out.println("=====================================================");

        //Get Document IDs
        List<String> documentIds = new ArrayList<>(documents.getDocuments().keySet());
        System.out.println("col1 Documents IDs");
        System.out.println(documentIds);
        System.out.println("=====================================================");


        //Get a subset of col1 documents by their IDs
        System.out.println("Get a subset of col1 documents by their IDs");
        List<Document> documents1 = dao.readDocumentsByIDs("db1","col1",documentIds.subList(0,2));
        System.out.println(documents1);
        System.out.println("=====================================================");


        //Delete a document
        System.out.println("Delete a document");
        String id = documents1.get(0).getId();
        if(dao.deleteDocument("db1","col1",documents1.get(0)))
            System.out.println("document with id : " + id + " has been deleted successfully!");
        System.out.println("=====================================================");

        System.out.println("col1 at db1 after deletion");
        DocumentsCollection documents2 = dao.readCollection("db1","col1");
        System.out.println(documents2);
        System.out.println();
        System.out.println("=====================================================");

        //Update a document
        System.out.println("Update a document");
        Document oldDocument = documents1.get(1);
        System.out.println(oldDocument);
        Document newDocument = DocumentMapper.jsonStringToDocument("{\"name\" : \"mohammed\", \"age\" : 23}");
        newDocument.getDocument().put("_id",oldDocument.getId());
        newDocument.setId(oldDocument.getId());
        System.out.println("Document old content : " + oldDocument);
        System.out.println("Document new content : " + newDocument);
        System.out.println();
        System.out.println();
        dao.updateDocument("db1","col1",oldDocument,newDocument);
        System.out.println("Document with id (" + oldDocument.getId() + ")" + " has been successfully updated!");
        System.out.println("its new content -->" + dao.readDocumentByID("db1","col1",oldDocument.getId()));
    }
}
