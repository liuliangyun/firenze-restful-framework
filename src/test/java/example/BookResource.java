package example;

import annotations.*;

@Path("/book")
public class BookResource {

    @GET
    public String getAllBooks () {
        return "getAllBooks";
    }

    @GET
    @Path("/{id}")
    public String getBook (@PathParam("id") String id) {
        return "getBook: " + id;
    }

    @POST
    public Book addBook (@RequestBody Book body) {
        return body;
    }

    @DELETE
    @Path("/{id}")
    public String deleteBook (@PathParam("id") String id) {
        return "deleteBook: " + id;
    }

    @PUT
    @Path("/{id}")
    public Book updateBook (@PathParam("id") String id, @RequestBody Book body) {
        return body;
    }

    @Path("/{id}")
    public AuthorResource getBookAuthor (@PathParam("id") String id) {
        return new AuthorResource();
    }
}
