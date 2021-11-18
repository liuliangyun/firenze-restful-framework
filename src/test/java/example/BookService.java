package example;


public class BookService {

    public String getAllBooks () {
        return "getAllBooks";
    }

    public String getBook (String id) {
        return "getBook: " + id;
    }

    public Book addBook (Book body) {
        return body;
    }

    public String deleteBook (String id) {
        return "deleteBook: " + id;
    }

    public Book updateBook (String id, Book body) {
        return body;
    }
}
