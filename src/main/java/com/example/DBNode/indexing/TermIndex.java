package com.example.DBNode.indexing;

import com.example.DBNode.api.model.Document;
import com.example.DBNode.utils.DocumentMapper;
import com.example.DBNode.utils.JsonToPropertyValueConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class TermIndex implements Index{
    private int count;
    private HashMap<String,HashSet<Integer>> postings;
    private HashMap<String,Document> documents;
    private HashMap<Integer,String> idIndex;
    private HashMap<String,Integer> reversedIdIndex;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ResultsIntersectionFinder intersectionFinder = new ResultsIntersectionFinder();

    public TermIndex(HashMap<String, Document> collectionData) {
        this.documents = collectionData;
        this.postings = new HashMap<>();
        this.idIndex = new HashMap<>();
        this.reversedIdIndex = new HashMap<>();
    }

    @Override
    public void buildIndex() throws JsonProcessingException {
        for(String documentID : documents.keySet()) {
            Document document = documents.get(documentID);
            addToIndex(document);
        }
    }
    @Override
    public void addToIndex(Document document) throws JsonProcessingException {
        if(document == null)
            throw new IllegalArgumentException();
        count++;
        idIndex.put(count,document.getId());
        reversedIdIndex.put(document.getId(),count);
        addValuesToIndex(DocumentMapper.DocumentToJsonString(document),count);
    }

    private void addValuesToIndex(String jsonObject, int id) throws JsonProcessingException {
        List<String> values = JsonToPropertyValueConverter.convert(jsonObject);
        System.out.println(values);
        for (String value : values){
            if(!postings.containsKey(value))
                postings.put(value,new HashSet<>());
            postings.get(value).add(id);
        }
    }


    @Override
    public void removeFromIndex(Document document) throws JsonProcessingException {
        if(document == null)
            throw new IllegalArgumentException();
        int countToRemove = reversedIdIndex.get(document.getId());
        idIndex.remove(countToRemove,document.getId());
        reversedIdIndex.remove(document.getId());
        deleteValuesFromIndex(DocumentMapper.DocumentToJsonString(document),count);
    }

    private void deleteValuesFromIndex(String jsonObject, int id) throws JsonProcessingException {
        List<String> values = JsonToPropertyValueConverter.convert(jsonObject);
        System.out.println(values);
        for (String value : values){
            if(postings.containsKey(value))
                postings.get(value).remove(id);
            if(postings.get(value).size() == 0)
                postings.remove(value);
        }
    }

    @Override
    public List<String> findBySingleValue(String value) {
        if(value == null)
            throw new IllegalArgumentException();
        value = value.toLowerCase().intern();
        HashSet<Integer> result = new HashSet<>();
        if(postings.containsKey(value))
            result = postings.get(value);
        if(result.size() == 0)
            return Collections.emptyList();
        return getFinalResult(result);
    }

    @Override
    public List<String> findByMultiValues(List<String> values) {
        if(values == null)
            throw new IllegalArgumentException();
        List<HashSet<Integer>> results = new ArrayList<>();
        for(String value : values){
            String value1 = value.toLowerCase().intern();
            if(!postings.containsKey(value1))
                return Collections.emptyList();
            results.add(postings.get(value1));
        }
        System.out.println(results);
        HashSet<Integer> finalResult = intersectionFinder.findIntersections(results);
        return getFinalResult(finalResult);
    }

    private List<String> getFinalResult(HashSet<Integer> idIndices){
        if(idIndices == null)
            throw new IllegalArgumentException();
        return idIndices.stream().map(index -> idIndex.get(index)).collect(Collectors.toList());
    }
}
