package com.example.dbnode.tests;

import com.example.dbnode.database.dao.DAO;
import com.example.dbnode.database.dao.DatabaseDAO;
import com.example.dbnode.api.model.Document;

import java.io.IOException;
import java.util.ArrayList;

public class DaoTest1 {
    public static void main(String[] args) throws IOException {
        DAO dao = new DatabaseDAO();
        System.out.println(dao.addDocument("db","col",new Document()));
        System.out.println(dao.deleteCollection("db","col"));
        System.out.println(dao.deleteDatabase("db"));
        System.out.println(dao.deleteDocument("db","col",new Document()));
        System.out.println(dao.updateDocument("db","col",new Document(),new Document()));

        System.out.println(dao.listDatabases());
        System.out.println(dao.listCollections("db"));
        System.out.println(dao.countDocuments("",""));
        System.out.println(dao.readCollection("",""));
        System.out.println(dao.readDocumentByID("","",""));
        System.out.println(dao.readDocumentsByIDs("","",new ArrayList<>()));
    }
}
