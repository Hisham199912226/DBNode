package com.example.DBNode.indexing;

import com.example.DBNode.model.Document;
import com.example.DBNode.utils.DocumentMapper;
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
    private ObjectMapper objectMapper;
    private ResultsIntersectionFinder intersectionFinder;

    public TermIndex(HashMap<String, Document> collectionData) {
        this.documents = collectionData;
        this.postings = new HashMap<>();
        this.idIndex = new HashMap<>();
        this.reversedIdIndex = new HashMap<>();
        this.objectMapper = new ObjectMapper();
        this.intersectionFinder = new ResultsIntersectionFinder();
    }

    @Override
    public void buildIndex() throws JsonProcessingException {
        for(String documentID : documents.keySet()) {
            Document document = documents.get(documentID);
            //System.out.println(document);
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
        JsonNode jsonNode = objectMapper.readTree(DocumentMapper.DocumentToJsonString(document));
        traverseToAdd(jsonNode,count,"");
    }

    private void traverseToAdd(JsonNode node, int id, String propertyName){
        if(node == null || propertyName == null)
            throw new IllegalArgumentException();
        if (node.isObject()) {
            // Handle object properties
            for (Iterator<String> it = node.fieldNames(); it.hasNext(); ) {
                propertyName = it.next();
                JsonNode propertyNode = node.get(propertyName);
                if(propertyName.equals("_id"))
                    continue;
                //System.out.println("Property: " + propertyName);
                traverseToAdd(propertyNode,id,propertyName);
            }
        } else if (node.isArray()) {
            // Handle array elements
            for (JsonNode elementNode : node) {
                traverseToAdd(elementNode,id,propertyName);
            }
        } else {
            // Handle scalar values
            String[] splitValue = node.asText().split("\\s+");
            for(String value : splitValue){
                value  = propertyName.concat("==").concat(value).toLowerCase();
                if(!postings.containsKey(value))
                    postings.put(value,new HashSet<>());
                postings.get(value).add(id);
            }
        }

    }

    @Override
    public void removeFromIndex(Document document) throws JsonProcessingException {
        if(document == null)
            throw new IllegalArgumentException();
        int countToRemove = reversedIdIndex.get(document.getId());
        idIndex.remove(countToRemove,document.getId());
        reversedIdIndex.remove(document.getId());
        JsonNode jsonNode = objectMapper.readTree(DocumentMapper.DocumentToJsonString(document));
        traverseToDelete(jsonNode,count,"");
    }

    private void traverseToDelete(JsonNode node, int id, String propertyName){
        if(node == null || propertyName == null)
            throw new IllegalArgumentException();
        if (node.isObject()) {
            // Handle object properties
            for (Iterator<String> it = node.fieldNames(); it.hasNext(); ) {
                propertyName = it.next();
                JsonNode propertyNode = node.get(propertyName);
                if(propertyName.equals("_id"))
                    continue;
                //System.out.println("Property: " + propertyName);
                traverseToDelete(propertyNode,id,propertyName);
            }
        } else if (node.isArray()) {
            // Handle array elements
            for (JsonNode elementNode : node) {
                traverseToDelete(elementNode,id,propertyName);
            }
        } else {
            // Handle scalar values
            String[] splitValue = node.asText().split("\\s+");
            for(String value : splitValue){
                value  = propertyName.concat("==").concat(value).toLowerCase();
                if(postings.containsKey(value))
                     postings.get(value).remove(id);
                if(postings.get(value).size() == 0)
                    postings.remove(value);
            }
        }

    }

    @Override
    public List<String> findBySingleValue(String value) {
        if(value == null)
            throw new IllegalArgumentException();
        value = value.toLowerCase();
        HashSet<Integer> result = new HashSet<>();
        if(postings.containsKey(value))
            result = postings.get(value.toLowerCase());
        if(result.size() == 0)
            return Collections.emptyList();
        return getFindResult(result);
    }

    @Override
    public List<String> findByMultiValues(List<String> values) {
        if(values == null)
            throw new IllegalArgumentException();
        List<HashSet<Integer>> results = new ArrayList<>();
        for(String value : values){
            String value1 = value.toLowerCase();
            if(!postings.containsKey(value1))
                return Collections.emptyList();
            System.out.println(value1 + " " + postings.get(value1));
            results.add(postings.get(value1));
        }
        System.out.println(results);
        HashSet<Integer> finalResult = intersectionFinder.findIntersections(results);
        return getFindResult(finalResult);
    }

    private List<String> getFindResult(HashSet<Integer> idIndices){
        if(idIndices == null)
            throw new IllegalArgumentException();
        return idIndices.stream().map(index -> idIndex.get(index)).collect(Collectors.toList());
    }
}
