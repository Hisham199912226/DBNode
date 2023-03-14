package com.example.DBNode.tests.threadingTests.ioTests;

import com.example.DBNode.database.io.IO;
import com.example.DBNode.database.io.IOOperations;

import java.io.File;
import java.io.IOException;

public class GetFileWithThreads {
    static IO io = new IOOperations();
    public static void main(String[] args) throws InterruptedException {

        io.createDirectory("./","dir2");
        try {
            io.createFile("./dir2/","file1",Thread.currentThread().getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Thread t1 = new Thread(() -> {
           File file = io.getFileByName("./dir2/","file1");
           System.out.println(Thread.currentThread().getName() + "read " +file.getName());
        });

        Thread t2 = new Thread(() -> {
            File file = io.getFileByName("./dir2/","file1");
            System.out.println(Thread.currentThread().getName() + "read " +file.getName());
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
