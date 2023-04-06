package com.example.dbnode.tests.threadingTests.ioTests;

import com.example.dbnode.database.io.IO;
import com.example.dbnode.database.io.IOOperations;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FileCreationWithThreads {
    static IO io = new IOOperations();
    static AtomicInteger integer = new AtomicInteger(1);

    public static void main(String[] args)  {


        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++){
            executorService.submit(() -> {
                int dirNumber = integer.getAndIncrement();
                String dirName = "dir" + dirNumber;
                String fileName = "file" + dirNumber;
                try {
                    io.createFile("./directories/"+dirName,fileName,Thread.currentThread().getName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }
        executorService.shutdown();
    }
}
