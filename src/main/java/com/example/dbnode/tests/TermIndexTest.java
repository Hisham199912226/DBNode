package com.example.dbnode.tests;

import com.example.dbnode.database.dao.DAO;
import com.example.dbnode.database.dao.DatabaseDAO;
import com.example.dbnode.api.client.model.Document;
import com.example.dbnode.api.client.model.DocumentsCollection;
import com.example.dbnode.indexing.TermIndex;
import com.example.dbnode.utils.DocumentMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TermIndexTest {
    public static void main(String[] args) throws IOException {

        DAO dao = new DatabaseDAO();
        dao.createDatabase("invertedIndexDB");
        dao.createCollection("invertedIndexDB","col1");
        String json1 = "{ \"name\": \"John Doe\", \"age\": 30}";
        String json2 = "{ \"name\": \"Jack Roi\", \"age\": 22}";
        String json3 = "{ \"name\": \"Jemmy Ron\", \"age\": 30}";
        String json4 = "{ \"name\": \"Joy young\", \"age\": 30}";
        String json5 = "{ \"name\": \"Gray Sam\", \"age\": 20}";
        String json6 = "{ \"name\": \"Jack Joy\", \"age\": 20}";
        dao.generateIdAndAddDocument("invertedIndexDB","col1", DocumentMapper.jsonStringToDocument(json1));
        dao.generateIdAndAddDocument("invertedIndexDB","col1", DocumentMapper.jsonStringToDocument(json2));
        dao.generateIdAndAddDocument("invertedIndexDB","col1", DocumentMapper.jsonStringToDocument(json3));
        dao.generateIdAndAddDocument("invertedIndexDB","col1", DocumentMapper.jsonStringToDocument(json4));
        dao.generateIdAndAddDocument("invertedIndexDB","col1", DocumentMapper.jsonStringToDocument(json5));
        dao.generateIdAndAddDocument("invertedIndexDB","col1", DocumentMapper.jsonStringToDocument(json6));

        DocumentsCollection documentsCollection = dao.readCollection("invertedIndexDB","col1");
        TermIndex termIndex = new TermIndex(documentsCollection.getDocuments());
        termIndex.buildIndex();

        System.out.println();
        System.out.println(termIndex.getIdIndex());
        System.out.println(termIndex.getPostings());

        System.out.println();
        System.out.println();

        System.out.println("===========================Single Value Searches=====================");
        System.out.println("Search for \"name:John\":");
        List<String> searchForJohnResult1 = termIndex.findBySingleValue("name:john");
        System.out.println(dao.readDocumentsByIDs("invertedIndexDB","col1",searchForJohnResult1));

        System.out.println();
        System.out.println();

        System.out.println("Search for \"name:Jack\":");
        List<String> searchForJohnResult2 = termIndex.findBySingleValue("name:jack");
        System.out.println(dao.readDocumentsByIDs("invertedIndexDB","col1",searchForJohnResult2));

        System.out.println();
        System.out.println();

        System.out.println("Search for \"age:30\":");
        List<String> searchForJohnResult3 = termIndex.findBySingleValue("age:30");
        System.out.println(dao.readDocumentsByIDs("invertedIndexDB","col1",searchForJohnResult3));

        System.out.println();
        System.out.println();



        System.out.println("===========================Multi Values Searches=====================");

        System.out.println("Search for \"name:Jack\" && \"age:30\":");
        List<String> values = new ArrayList<>();
        values.add("name:jack");
        values.add("age:30");
        List<String> searchForJohnResult4 = termIndex.findByMultiValues(values);
        System.out.println(dao.readDocumentsByIDs("invertedIndexDB","col1",searchForJohnResult4));

        System.out.println();
        System.out.println();

        System.out.println(termIndex.getIdIndex());
        System.out.println(termIndex.getPostings());


        System.out.println();
        System.out.println();
        System.out.println("Search for \"name:jack\" && \"age:20\":");
        List<String> values1 = new ArrayList<>();
        values1.add("{name:jack}");
        values1.add("{age:20}");
        List<String> searchForJohnResult5 = termIndex.findByMultiValues(values1);
        System.out.println(dao.readDocumentsByIDs("invertedIndexDB","col1",searchForJohnResult5));


        System.out.println();
        System.out.println();

        System.out.println("Search for \"name:jack\" && \"age:22\":");
        List<String> values2 = new ArrayList<>();
        values2.add("name:jack");
        values2.add("age:22");
        List<String> searchForJohnResult6 = termIndex.findByMultiValues(values2);
        System.out.println(dao.readDocumentsByIDs("invertedIndexDB","col1",searchForJohnResult6));



    }
}
