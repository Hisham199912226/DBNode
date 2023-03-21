package com.example.dbnode.database.io;


import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


public class IOOperations implements IO{
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();


    @Override
    public boolean createDirectory(String path,String dirName) throws SecurityException{
        if(path == null || dirName == null)
            throw new IllegalArgumentException();
        try {
            writeLock.lock();
            File dir = new File(path,dirName);
            if(!dir.exists()){
                return dir.mkdir();
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean deleteDirectory(String path, String dirName) {
        if (path == null || dirName == null) {
            throw new IllegalArgumentException();
        }
        try {
            writeLock.lock();
            File dir = new File(path, dirName);
            if (dir.exists()) {
                deleteDirectory(dir);
                return true;
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    private void deleteDirectory(File directory) {
        Deque<File> stack = new ArrayDeque<>();
        stack.push(directory);
        while (!stack.isEmpty()) {
            File current = stack.pop();
            File[] files = current.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        stack.push(file);
                    } else {
                        file.delete();
                    }
                }
            }
            current.delete();
        }
    }

    @Override
    public boolean createFile(String path, String fileName, String content) throws IOException {
        if(path == null || fileName == null || content == null)
            throw new IllegalArgumentException();
        File file = new File(path,fileName.concat(".txt"));
        if(file.exists())
            return false;
        if(!isPathExist(path))
            return false;
        try {
            writeLock.lock();
            try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))){
                bufferedWriter.write(content);
                bufferedWriter.flush();
            }
        }
         finally {
            writeLock.unlock();
        }
        return true;
    }

    private boolean isPathExist(String path){
        File file = new File(path,"");
        return file.exists();
    }

    @Override
    public boolean deleteFile(String path, String fileName) {
        if(path == null || fileName == null)
            throw new IllegalArgumentException();
        if(!isPathExist(path))
            return false;
        try {
            writeLock.lock();
            File file = new File(path,fileName.concat(".txt"));
            return file.delete();

        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean updateFile(String path, String fileName, String newContent) throws IOException {
        File fileToUpdate = getFileByName(path,fileName);
        if(!isPathExist(path))
            return false;
        try{
            writeLock.lock();
            if(!fileToUpdate.exists())
                return false;
            try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileToUpdate));) {
                bufferedWriter.write(newContent);
                bufferedWriter.flush();
            }
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public File getFileByName(String path, String fileName) {
        if(path == null || fileName == null)
            throw new IllegalArgumentException();
        try {
            readLock.lock();
            return new File(path,fileName.concat(".txt"));
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<File> getDirectories(String path, String dirName) {
        if(path == null || dirName == null)
            throw new IllegalArgumentException();
        try{
            readLock.lock();
            File dir = new File(path,dirName);
            if(dir.exists()) {
                File[] directoryChildren = dir.listFiles();
                if (directoryChildren != null) {
                    return directoriesFilter(directoryChildren);
                }
            }
            return Collections.emptyList();
        } finally {
            readLock.unlock();
        }

    }
    private List<File> directoriesFilter(File[] directoryChildren){
        if(directoryChildren == null)
            throw new IllegalArgumentException();
        List<File> directories = new ArrayList<>();
        for(File file : directoryChildren) {
            if (file.isDirectory())
                directories.add(file);
        }
        return directories;
    }

    @Override
    public List<String> getDirectoriesNames(String path, String dirName) {
        try{
            readLock.lock();
            return getDirectories(path,dirName).stream().map(File::getName).collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<File> getFiles(String path, String dirName) {
        if(path == null || dirName == null)
            throw new IllegalArgumentException();
        try{
            readLock.lock();
            File dir = new File(path,dirName);
            if(dir.exists()) {
                File[] directoryChildren = dir.listFiles();
                if (directoryChildren != null) {
                    return filesFilter(directoryChildren);
                }
            }
            return Collections.emptyList();
        } finally {
            readLock.unlock();
        }

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
        try{
            readLock.lock();
            return getFiles(path,dirName).stream().map(File::getName).collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int getFilesCount(String path, String dirName) {
        try{
            readLock.lock();
            return getFilesNames(path,dirName).size();
        } finally {
            readLock.unlock();
        }
    }

}
