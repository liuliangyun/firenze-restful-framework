package rest.example;

import rest.annotations.*;

import java.util.Objects;

@Path("/author")
public class AuthorResource {

    private String book;

    public void setBook(String book) {
        this.book = book;
    }

    @GET
    public String getAllAuthors() {
        if (Objects.isNull(book)) {
            return "getAllAuthors";
        }
        return "getBookAuthors";
    }

    @GET
    @Path("/{id}")
    public String getAuthor (@PathParam("id") String id) {
        if (Objects.isNull(book)) {
            return "getAuthor: " + id;
        }
        return "getBookAuthor: " + id;
    }

    @POST
    public Author addAuthor (@RequestBody Author body) {
        return body;
    }
}
