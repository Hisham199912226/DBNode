package com.example.DBNode.tests.threadingTests.ioTests;

import com.example.DBNode.database.io.IO;
import com.example.DBNode.database.io.IOOperations;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FileDeletionWithThreads {
    static IO io = new IOOperations();
    static AtomicInteger integer = new AtomicInteger(1);

    public static void main(String[] args)  {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++){
            executorService.submit(() -> {
                int dirNumber = integer.getAndIncrement();
                String dirName = "dir" + dirNumber;
                String fileName = "file" + dirNumber + ".txt";
                io.deleteFile("./directories/"+dirName,fileName);
            });

        }
        executorService.shutdown();
    }
}
