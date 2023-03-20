package com.example.dbnode.tests.threadingTests.daoTests;

import com.example.dbnode.database.dao.DAO;
import com.example.dbnode.database.dao.DatabaseDAO;
import com.example.dbnode.api.model.DocumentsCollection;
import com.example.dbnode.utils.DocumentMapper;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadCollectionWithThreads {
    public static void main(String[] args) throws IOException {
        DAO dao = new DatabaseDAO();
        dao.createDatabase("DB");
        dao.createCollection("DB","col1");
        String json1 = "{ \"name\": \"John Doe\", \"age\": 30}";
        String json2 = "{ \"name\": \"Jack Roi\", \"age\": 22}";
        String json3 = "{ \"name\": \"Jemmy Ron\", \"age\": 30}";
        String json4 = "{ \"name\": \"Joy young\", \"age\": 30}";
        String json5 = "{ \"name\": \"Gray Sam\", \"age\": 20}";
        String json6 = "{ \"name\": \"Jack Joy\", \"age\": 20}";
        dao.addDocument("DB","col1", DocumentMapper.jsonStringToDocument(json1));
        dao.addDocument("DB","col1", DocumentMapper.jsonStringToDocument(json2));
        dao.addDocument("DB","col1", DocumentMapper.jsonStringToDocument(json3));
        dao.addDocument("DB","col1", DocumentMapper.jsonStringToDocument(json4));
        dao.addDocument("DB","col1", DocumentMapper.jsonStringToDocument(json5));
        dao.addDocument("DB","col1", DocumentMapper.jsonStringToDocument(json6));

        ExecutorService service = Executors.newFixedThreadPool(10);

        for(int i = 0 ; i < 20 ; i++){
            service.submit(() -> {
                DocumentsCollection documentsCollection = dao.readCollection("DB","col1");
                for(String docId : documentsCollection.getDocuments().keySet()){
                    System.out.println(documentsCollection.getDocuments().get(docId));
                }
                System.out.println(Thread.currentThread().getName().concat(" read col1 successfully!"));
            });
        }

        service.shutdown();


    }
}
