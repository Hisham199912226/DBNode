package com.example.DBNode.database.io;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class IOOperations implements IO{
    @Override
    public boolean createDirectory(String path,String dirName) throws SecurityException{
        if(path == null || dirName == null)
            throw new IllegalArgumentException();
        File dir = new File(path,dirName);
        if(!dir.exists()){
            return dir.mkdir();
        }
        return false;
    }

    @Override
    public boolean deleteDirectory(String path, String dirName) {
        if(path == null || dirName == null)
            throw new IllegalArgumentException();
        File dir = new File(path,dirName);
        if(dir.exists()){
            return dir.delete();
        }
        return false;
    }

    @Override
    public boolean createFile(String path, String fileName, String content) throws IOException {
        if(path == null || fileName == null || content == null)
            throw new IllegalArgumentException();
        File file=new File(path,fileName.concat(".txt"));
        if(file.exists())
            return false;
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))){
            bufferedWriter.write(content);
            bufferedWriter.flush();
        }
        return true;
    }

    @Override
    public boolean deleteFile(String path, String fileName) {
        if(path == null || fileName == null)
            throw new IllegalArgumentException();
        File file = new File(path,fileName.concat(".txt"));
        return file.delete();
    }

    @Override
    public File getFileByName(String path, String fileName) {
        if(path == null || fileName == null)
            throw new IllegalArgumentException();
        return new File(path,fileName.concat(".txt"));
    }

    @Override
    public List<File> getDirectories(String path, String dirName) {
        if(path == null || dirName == null)
            throw new IllegalArgumentException();
        File dir = new File(path,dirName);
        if(dir.exists()) {
            File[] directoryChildren = dir.listFiles();
            if (directoryChildren != null) {
                return directoriesFilter(directoryChildren);
            }
        }
        return Collections.emptyList();
    }

    private List<File> directoriesFilter(File[] directoryChildren){
        if(directoryChildren == null)
            throw new IllegalArgumentException();
        List<File> directories = new ArrayList<>();
        for(File file : directoryChildren){
            if(file.isDirectory())
                directories.add(file);
        }
        return directories;
    }

    @Override
    public List<String> getDirectoriesNames(String path, String dirName) {
        return getDirectories(path,dirName).stream().map(File::getName).collect(Collectors.toList());
    }

    @Override
    public List<File> getFiles(String path, String dirName) {
        if(path == null || dirName == null)
            throw new IllegalArgumentException();
        File dir = new File(path,dirName);
        if(dir.exists()) {
            File[] directoryChildren = dir.listFiles();
            if (directoryChildren != null) {
                return filesFilter(directoryChildren);
            }
        }
        return Collections.emptyList();
    }

    private List<File> filesFilter(File[] directoryChildren){
        if(directoryChildren == null)
            throw new IllegalArgumentException();
        List<File> files = new ArrayList<>();
        for(File file : directoryChildren){
            if(file.isFile())
                files.add(file);
        }
        return files;
    }

    @Override
    public List<String> getFilesNames(String path, String dirName) {
        return getFiles(path,dirName).stream().map(File::getName).collect(Collectors.toList());
    }

    @Override
    public int getFilesCount(String path, String dirName) {
       return getFilesNames(path,dirName).size();
    }


}
