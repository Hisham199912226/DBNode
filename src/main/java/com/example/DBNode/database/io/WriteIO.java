package com.example.DBNode.database.io;

import java.io.IOException;

public interface WriteIO {
    boolean createDirectory(String path,String dirName);
    boolean deleteDirectory(String path, String dirName);
    boolean createFile(String path, String fileName, String content) throws IOException;
    boolean deleteFile(String path, String fileName);
    boolean updateFile(String path, String fileName,String newContent) throws IOException;
}
