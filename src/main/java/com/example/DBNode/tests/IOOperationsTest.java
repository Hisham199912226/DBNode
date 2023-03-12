package com.example.DBNode.tests;

import com.example.DBNode.database.io.IOOperations;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class IOOperationsTest {
    public static void main(String[] args) throws IOException {
        IOOperations io = new IOOperations();

        io.createDirectory("./","IOTest");
        io.createDirectory("./IOTest/","parentDir");
        io.createDirectory("./IOTest/parentDir","childDir1");
        io.createDirectory("./IOTest/parentDir","childDir2");
        io.createDirectory("./IOTest/parentDir","childDir3");
        io.createFile("./IOTest/parentDir/childDir1","file1","{\"name\" : \"hisham\"}");
        io.createFile("./IOTest/parentDir/childDir1","file2","{\"name\" : \"hisham\"}");
        io.createFile("./IOTest/parentDir/childDir1","file3","{\"name\" : \"hisham\"}");
        io.createFile("./IOTest/parentDir/childDir2","file2","{\"name\" : \"hisham\"}");
        io.createFile("./IOTest/parentDir/childDir3","file3","{\"name\" : \"hisham\"}");

        File file = io.getFileByName("./IOTest/parentDir/childDir1","file1");
        System.out.print(file.getName());   //file1

        System.out.println();
        System.out.println();

        List<String> directories = io.getDirectoriesNames("./IOTest/","parentDir");
        for (String dirName : directories)
            System.out.print(dirName + " ");  // chileDir1 chileDir2 chileDir3

        System.out.println();
        System.out.println();

        List<String> files = io.getFilesNames("./IOTest/parentDir/","childDir1");
        for (String fileName : files)
            System.out.print(fileName + " "); //file1.txt file2.txt file3.txt

        System.out.println();
        System.out.println();

        int fileCounts = io.getFilesCount("./IOTest/parentDir/","childDir1");
        System.out.println(fileCounts);  //3

    }
}
