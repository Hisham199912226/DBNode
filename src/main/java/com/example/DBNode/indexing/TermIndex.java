package com.example.DBNode.indexing;

import com.example.DBNode.api.model.Document;
import com.example.DBNode.utils.DocumentMapper;
import com.example.DBNode.utils.JsonToPropertyValueConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Data
public class TermIndex implements Index{
    private int count;
    private HashMap<String,HashSet<Integer>> postings;
    private ConcurrentHashMap<String,Document> documents;
    private HashMap<Integer,String> idIndex;
    private HashMap<String,Integer> reversedIdIndex;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ResultsIntersectionFinder intersectionFinder = new ResultsIntersectionFinder();
    private final ReentrantReadWriteLock lock;

    public TermIndex(ConcurrentHashMap<String, Document> collectionData) {
        this.documents = collectionData;
        this.postings = new HashMap<>();
        this.idIndex = new HashMap<>();
        this.reversedIdIndex = new HashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public void buildIndex() throws JsonProcessingException {
        lock.writeLock().lock();
        try {
            for(String documentID : documents.keySet()) {
                Document document = documents.get(documentID);
                addToIndex(document);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void addToIndex(Document document) throws JsonProcessingException {
        if(document == null)
            throw new IllegalArgumentException();

        lock.writeLock().lock();
        try {
            count++;
            idIndex.put(count,document.getId());
            reversedIdIndex.put(document.getId(),count);
            addValuesToIndex(DocumentMapper.DocumentToJsonString(document),count);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void addValuesToIndex(String jsonObject, int id) throws JsonProcessingException {
        List<String> values = JsonToPropertyValueConverter.convert(jsonObject);

        lock.writeLock().lock();
        try {
            for (String value : values){
                if(!postings.containsKey(value))
                    postings.put(value,new HashSet<>());
                postings.get(value).add(id);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


    @Override
    public void removeFromIndex(Document document) throws JsonProcessingException {
        if(document == null)
            throw new IllegalArgumentException();

        lock.writeLock().lock();
        try {
            int countToRemove = reversedIdIndex.get(document.getId());
            idIndex.remove(countToRemove,document.getId());
            reversedIdIndex.remove(document.getId());
            deleteValuesFromIndex(DocumentMapper.DocumentToJsonString(document),countToRemove);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void deleteValuesFromIndex(String jsonObject, int id) throws JsonProcessingException {
        List<String> values = JsonToPropertyValueConverter.convert(jsonObject);
        lock.writeLock().lock();
        try {
            for (String value : values){
                if(postings.containsKey(value))
                    postings.get(value).remove(id);
                if(postings.get(value) != null && postings.get(value).size() == 0)
                    postings.remove(value);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<String> findBySingleValue(String value) {
        if(value == null)
            throw new IllegalArgumentException();
        value = value.toLowerCase().intern();
        lock.readLock().lock();
        try {
            HashSet<Integer> result = new HashSet<>();
            if(postings.containsKey(value))
                result = new HashSet<>(postings.get(value));
            if(result.size() == 0)
                return Collections.emptyList();
            return getFinalResult(result);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<String> findByMultiValues(List<String> values) {
        if(values == null)
            throw new IllegalArgumentException();
        List<HashSet<Integer>> results = new ArrayList<>();
        lock.readLock().lock();
        try {
            for(String value : values){
                String value1 = value.toLowerCase().intern();
                if(!postings.containsKey(value1))
                    return Collections.emptyList();
                results.add(new HashSet<>(postings.get(value1)));
            }
        } finally {
            lock.readLock().unlock();
        }
        HashSet<Integer> finalResult = intersectionFinder.findIntersections(results);
        lock.readLock().lock();
        try {
            return getFinalResult(finalResult);
        } finally {
            lock.readLock().unlock();
        }
    }

    private List<String> getFinalResult(HashSet<Integer> idIndices){
        if(idIndices == null)
            throw new IllegalArgumentException();
        lock.readLock().lock();
        try {
            return idIndices.stream().map(index -> idIndex.get(index)).collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
}
