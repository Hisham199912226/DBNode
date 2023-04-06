package com.example.dbnode.tests.threadingTests.ioTests;

import com.example.dbnode.database.io.IO;
import com.example.dbnode.database.io.IOOperations;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FileUpdatingWithThreads {

    static IO io = new IOOperations();
    static AtomicInteger integer = new AtomicInteger(1);

    public static void main(String[] args) throws IOException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        io.createDirectory("./","dir10");
        io.createFile("./dir10","file1","initial content");
        //Try to update file1.txt at dir10 (threads will sleep randomly up to 5 sec)
        for (int i = 0; i < 10; i++){
            executorService.submit(() -> {
                String dirName = "dir10";
                String fileName = "file1";
                try {
                    Thread.sleep(new Random().nextInt(5000));
                    io.updateFile("./"+dirName,fileName,Thread.currentThread().getName() + ": file updated!");
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

        }
        executorService.shutdown();
    }
}
