package com.example.dbnode.database.io;

import java.io.File;
import java.util.List;

public interface ReadIO {
    List<File> getDirectories(String path, String dirName);
    List<String> getDirectoriesNames(String path,String dirName);
    List<File> getFiles(String path,String dirName);
    List<String> getFilesNames(String path,String dirName);
    File getFileByName(String path, String fileName);
    int getFilesCount(String path, String dirName);
}
