package ru.avalon.javapp.devj130.databasetest;

public interface IDbService extends AutoCloseable{
    boolean addAuthor(Author author) throws DocumentException;
    boolean addDocument(Document doc, Author author) throws
            DocumentException;
    Document[] findDocumentByAuthor(Author author) throws
            DocumentException;
    Document[] findDocumentByContent(String content) throws
            DocumentException;
    boolean deleteAuthor(Author author) throws
            DocumentException;
    boolean deleteAuthor(int id) throws DocumentException;

}
