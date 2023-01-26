package ru.avalon.javapp.devj130.databasetest;


import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws DocumentException {
        DbServer dbServer = new DbServer();
        Author author = new Author(4, "Hi!", "  vcna vnnv nv ");

        dbServer.addAuthor(author);
//        Document document = new Document(4, "bvhb", "bb9990vh", 3);
//        dbServer.addDocument(document, author);
//
//
//        Document[] doc = dbServer.findDocumentByAuthor(author);
//        System.out.println(Arrays.toString(doc));
//        System.out.println(dbServer.addAuthor(author));
//        Document[] doc2 = dbServer.findDocumentByContent("bvhb");
//        System.out.println(Arrays.toString(doc2));
//        System.out.println(dbServer.deleteAuthor(author));


    }
}
