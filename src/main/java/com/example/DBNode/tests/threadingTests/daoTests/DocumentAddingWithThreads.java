package com.example.DBNode.tests.threadingTests.daoTests;

import com.example.DBNode.database.dao.DAO;
import com.example.DBNode.database.dao.DatabaseDAO;
import com.example.DBNode.utils.DocumentMapper;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class DocumentAddingWithThreads {

    static AtomicInteger integer = new AtomicInteger(1);
    public static void main(String[] args) throws InterruptedException, IOException {
        DAO dao = new DatabaseDAO();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        dao.createDatabase("DB1");
        dao.createCollection("DB1","Col1");

        for (int i = 0; i < 10; i++){
            executorService.submit(() -> {
                String jsonObject = "{ \"name\": \"Alice\", \"age\": 30, \"isMarried\": false }";
                String databaseName = "DB1";
                String collectionName = "Col1";
                try {
                   dao.addDocument(databaseName,collectionName, DocumentMapper.jsonStringToDocument(jsonObject));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }
        executorService.shutdown();
    }
}
