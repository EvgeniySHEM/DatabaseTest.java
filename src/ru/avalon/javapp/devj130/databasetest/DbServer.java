package ru.avalon.javapp.devj130.databasetest;

import java.sql.*;
import java.util.*;

public class DbServer implements IDbService {

    private static final String dbURL = "jdbc:mysql://localhost:3306/demo";
    private static final String dbUserName = "root";
    private static final String dbPassword = "12345678";

    /**
     * Метод добавляет нового автора к базе данных, если все
     * обязательные поля объекта author определены. В противном случае, метод пытается обновить
     * уже существующие записи, используя заполненные поля класса для поиска подходящих
     * записей. Например, если в объекте author указан id автора, поле имени автора пусто, а поле примечаний содержит
     * текст, то у записи с заданным идентификатором обновляется поле примечаний.
     *
     * @param author именные данные автора.
     * @return возвращает значение true, если создана новая запись, и значение false, если обновлена существующая запись.
     * @throws DocumentException выбрасывается в случае, если поля объекта author заполнены неправильно и
     *                           не удаётся создать новую запись или обновить уже существующую. Данное исключение также выбрасывается в случае
     *                           общей ошибки доступа к базе данных
     */
    @Override
    public boolean addAuthor(Author author) throws DocumentException {
        if (author == null)
            throw new DocumentException("The author reference is null");

        try (Connection connection = DriverManager.getConnection(dbURL, dbUserName, dbPassword)) {
            Author checkingAuthor = null;
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM AUTHORS " +
                    "WHERE ID = ?")) {
                preparedStatement.setInt(1, author.getAuthor_id());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        checkingAuthor = new Author(resultSet.getInt("ID"),
                                resultSet.getString("AUTHOR_NAME"),
                                resultSet.getString("NOTES"));
                    }
                }
            }

            if (checkingAuthor == null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " +
                        "AUTHORS (ID, AUTHOR_NAME, NOTES) VALUES (?, ?, ?)")) {
                    preparedStatement.setInt(1, author.getAuthor_id());
                    preparedStatement.setString(2, author.getAuthor());
                    preparedStatement.setString(3, author.getNotes());
                    preparedStatement.executeUpdate();
                }
                return true;
            }

            if (!checkingAuthor.equals(author)) {
                if (Objects.equals(author.getAuthor(), "")) {
                    if (author.getNotes().equals(checkingAuthor.getNotes())) {
                        throw new DocumentException("The fields of the author object are filled in incorrectly");
                    }
                    try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE AUTHORS " +
                            "SET NOTES = ? WHERE ID = ?")) {
                        preparedStatement.setString(1, author.getNotes());
                        preparedStatement.setInt(2, author.getAuthor_id());
                        preparedStatement.executeUpdate();
                    }
                    return false;
                } else {
                    try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE AUTHORS " +
                            "SET AUTHOR_NAME = ? WHERE ID = ?")) {
                        preparedStatement.setString(1, author.getAuthor());
                        preparedStatement.setInt(2, author.getAuthor_id());
                        preparedStatement.executeUpdate();
                        return false;
                    }
                }
            } else {
                throw new DocumentException("The fields of the author object are filled in incorrectly");
            }

        } catch (SQLException e) {
            throw new DocumentException(e.toString());
        }
    }

    /**
     * Метод добавляет новый документ к базе данных, если все обязательные поля объектов doc и author определены.
     * В противном случае,метод пытается обновить уже существующие записи, используя заполненные поля объектов для
     * поиска подходящих записей.
     *
     * @param doc    добавляемый или обновляемый документ.
     * @param author ссылка на автора документа.
     * @return возвращает значение true, если создан новый документ, и значение false, если обновлена
     * уже существующая запись.
     * @throws DocumentException выбрасывается в случае, если поля объектов doc и author заполнены
     *                           неправильно и не удаётся создать новую запись или обновить уже существующую.
     *                           Данное исключение также выбрасывается в случае общей ошибки доступа к базе данных.
     */

    @Override
    public boolean addDocument(Document doc, Author author) throws DocumentException {
        if (doc == null)
            throw new DocumentException("The document reference is null");
        if (author == null)
            throw new DocumentException("The author reference is null");
        if (doc.getAuthor_id() != author.getAuthor_id())
            throw new DocumentException("Incorrect data entered");

        try (Connection connection = DriverManager.getConnection(dbURL, dbUserName, dbPassword)) {
            Document checkDocument = null;
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM DOCUMENTS " +
                    "WHERE ID = ?")) {
                preparedStatement.setInt(1, doc.getDocument_id());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        checkDocument = new Document(resultSet.getInt("ID"),
                                resultSet.getString("TITLE"), resultSet.getString("TEXT"),
                                resultSet.getInt("AUTHOR_ID"));
                    }
                }
            }

            if (checkDocument == null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO DOCUMENTS " +
                        "(ID, TITLE, TEXT, DATE_CREATION, AUTHOR_ID) " +
                        "VALUES (?, ?, ?, default, ?) ")) {
                    preparedStatement.setInt(1, doc.getDocument_id());
                    preparedStatement.setString(2, doc.getTitle());
                    preparedStatement.setString(3, doc.getText());
                    preparedStatement.setInt(4, doc.getAuthor_id());
                    preparedStatement.executeUpdate();
                    return true;
                }
            }
            if (checkDocument.equals(doc)) {
                if (checkDocument.getText().equals(doc.getText())) {
                    throw new DocumentException("The fields of the author object are filled in incorrectly");
                }
                try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE DOCUMENTS " +
                        "SET TEXT = ?, DATE_CREATION = default WHERE ID = ?")) {
                    preparedStatement.setString(1, doc.getText());
                    preparedStatement.setInt(2, doc.getDocument_id());
                    preparedStatement.executeUpdate();
                    return false;
                }
            } else if (!checkDocument.getTitle().equals(doc.getTitle())) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE DOCUMENTS " +
                        "SET TITLE = ?, DATE_CREATION = default WHERE ID = ?")) {
                    preparedStatement.setString(1, doc.getTitle());
                    preparedStatement.setInt(2, doc.getDocument_id());
                    preparedStatement.executeUpdate();
                    return false;
                }
            } else {
                throw new DocumentException("The fields of the document object are filled in incorrectly");
            }

        } catch (SQLException e) {
            throw new DocumentException(e.toString());
        }
    }

    /**
     * Метод производит поиск документов по их автору.
     *
     * @param author автор документа. Объект может содержать
     *               неполную информацию
     *               об авторе. Например, объект может содержать только
     *               именные данные автора
     *               или только его идентификатор.
     * @return возвращает массив всех найденных документов. Если
     * в базе данных
     * не найдено ни одного документа, то возвращается значение
     * null.
     * @throws DocumentException выбрасывается в случае, если
     *                           поле объекта
     *                           author заполнены неправильно или нелья выполнить поиск по
     *                           его полям.
     *                           Данное исключение также выбрасывается в случае общей
     *                           ошибки доступа к
     *                           базе данных
     */
    @Override
    public Document[] findDocumentByAuthor(Author author) throws DocumentException {
        if (author == null)
            throw new DocumentException("The author reference is null");

        List<Document> documentList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID, TITLE, TEXT, DATE_CREATION, AUTHOR_ID " +
                     "FROM DOCUMENTS " +
                     "WHERE AUTHOR_ID = ?")) {
            preparedStatement.setInt(1, author.getAuthor_id());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    documentList.add(new Document(resultSet.getInt(1), resultSet.getString(2),
                            resultSet.getString(3), resultSet.getDate(4), resultSet.getInt(5)));
                }
            }

        } catch (SQLException e) {
            throw new DocumentException(e.toString());
        }

        if (documentList.size() == 0) {
            return null;
        }
        return documentList.toArray(new Document[0]);
    }

    /**
     * Метод производит поиск документов по их содержанию.
     *
     * @param content фрагмент текста (ключевые слова), который
     *                должен
     *                содержаться в заголовке или в основном тексте документа.
     * @return возвращает массив найденных документов.Если в
     * базе данных не
     * найдено ни одного документа, удовлетворяющего условиям
     * поиска, то
     * возвращается значение null.
     * @throws DocumentException выбрасывается в случае, если
     *                           строка content
     *                           равна null или является пустой. Данное исключение также
     *                           выбрасывается в
     *                           случае общей ошибки доступа к базе данных
     */
    @Override
    public Document[] findDocumentByContent(String content) throws DocumentException {
        if (content == null || content.isEmpty())
            throw new DocumentException("The string is null or is empty");

        List<Document> documentList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID, TITLE, TEXT, DATE_CREATION, AUTHOR_ID " +
                     "FROM DOCUMENTS " +
                     "WHERE TITLE LIKE ? OR TEXT LIKE ?")) {
            preparedStatement.setString(1, "%" + content + "%");
            preparedStatement.setString(2, "%" + content + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    documentList.add(new Document(resultSet.getInt(1), resultSet.getString(2),
                            resultSet.getString(3), resultSet.getDate(4), resultSet.getInt(5)));
                }
            }

        } catch (SQLException e) {
            throw new DocumentException(e.toString());
        }
        if (documentList.size() == 0) {
            return null;
        }
        return documentList.toArray(new Document[0]);
    }

    /**
     * Метод удаляет автора из базы данных. Всесте с автором
     * удаляются и все
     * документы, которые ссылаются на удаляемого автора.
     *
     * @param author удаляемый автор. Объект может содержать
     *               неполные данные
     *               автора, например, только идентификатор автора.
     * @return значение true, если запись автора успешно
     * удалена, и значение
     * false - в противном случае.
     * @throws DocumentException выбрасывается в случае, если
     *                           поля объекта
     *                           author заполнены неправильно или ссылка author равна
     *                           null, а также случае
     *                           общей ошибки доступа к базе данных.
     */
    @Override
    public boolean deleteAuthor(Author author) throws DocumentException {
        if (author == null)
            throw new DocumentException("The author reference is null");
        int id = author.getAuthor_id();
        return searchAndDeleteAuthorById(id);
    }

    /**
     * Метод удаляет автора из базы данных по его
     * идентификатору. Всесте с
     * автором удаляются и все документы, которые ссылаются на
     * удаляемого
     * автора.
     *
     * @param id идентификатор удаляемого автора.
     * @return значение true, если запись автора успешно
     * удалена, и значение
     * false - в противном случае.
     * @throws DocumentException выбрасывается в случае общей
     *                           ошибки доступа к
     *                           базе данных.
     */
    @Override
    public boolean deleteAuthor(int id) throws DocumentException {
        return searchAndDeleteAuthorById(id);
    }

    @Override
    public void close() throws Exception {

    }

    private static boolean searchAndDeleteAuthorById(int id) throws DocumentException {
        try (Connection connection = DriverManager.getConnection(dbURL, dbUserName, dbPassword)) {
            Author checkingAuthor = null;
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM AUTHORS " +
                    "WHERE ID = ?")) {
                preparedStatement.setInt(1, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        checkingAuthor = new Author(resultSet.getInt("ID"),
                                resultSet.getString("AUTHOR_NAME"),
                                resultSet.getString("NOTES"));
                    }
                }
            }

            if (checkingAuthor != null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE D.*, A.* " +
                        "FROM DOCUMENTS D , AUTHORS A " +
                        "WHERE D.AUTHOR_ID = ?" +
                        "  AND D.AUTHOR_ID = A.ID")) {
                    preparedStatement.setInt(1, id);
                    preparedStatement.executeUpdate();

                    return true;
                }
            } else {
                throw new DocumentException("The fields of the author object are filled in incorrectly");
            }

        } catch (SQLException e) {
            throw new DocumentException(e.toString());
        }
    }

}
