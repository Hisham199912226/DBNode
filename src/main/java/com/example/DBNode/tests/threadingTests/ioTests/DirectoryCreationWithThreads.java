package com.example.DBNode.tests.threadingTests.ioTests;



import com.example.DBNode.database.io.IO;
import com.example.DBNode.database.io.IOOperations;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class DirectoryCreationWithThreads {
    static IO io = new IOOperations();
    static AtomicInteger integer = new AtomicInteger(1);
    public static void main(String[] args) {

        io.createDirectory("./","directories");
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++){
            executorService.submit(() -> {
                int dirNumber = integer.getAndIncrement();
                String dirName = "dir" + dirNumber;
                io.createDirectory("./directories",dirName);
            });
        }
        executorService.shutdown();
    }
}
