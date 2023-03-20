package com.example.dbnode.tests;


import com.example.dbnode.api.model.Document;
import com.example.dbnode.utils.DocumentMapper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DocumentMapperTest {
    public static void main(String[] args) throws IOException {

        String jsonObject = "{ \"name\": \"Alice\", \"age\": 30, \"isMarried\": false }";
        File dir = new File("./","dir");
        if(!dir.exists())
            dir.mkdir();

        File file = new File("./dir/","file.txt");
        if(!file.exists())
            file.createNewFile();

        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(jsonObject);
            bufferedWriter.flush();
        }

        Document document = DocumentMapper.fileToDocument(file);
        String jsonString = DocumentMapper.documentToJsonString(document);

        System.out.println(jsonString); //{ \"name\": \"Alice\", \"age\": 30, \"isMarried\": false }

        Document document1 = DocumentMapper.jsonStringToDocument(jsonString);

        System.out.println(document1.getDocument()); //{isMarried=false, name=Alice, age=30}



    }
}
