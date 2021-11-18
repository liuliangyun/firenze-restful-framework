package example;

import annotations.*;
import cdi.annotations.Inject;


@Path("/book")
public class BookResource {

    @Inject
    private BookService bookService;

    @GET
    public String getAllBooks () {
        return bookService.getAllBooks();
    }

    @GET
    @Path("/{id}")
    public String getBook (@PathParam("id") String id) {
        return bookService.getBook(id);
    }

    @POST
    public Book addBook (@RequestBody Book body) {
        return bookService.addBook(body);
    }

    @DELETE
    @Path("/{id}")
    public String deleteBook (@PathParam("id") String id) {
        return bookService.deleteBook(id);
    }

    @PUT
    @Path("/{id}")
    public Book updateBook (@PathParam("id") String id, @RequestBody Book body) {
        return bookService.updateBook(id, body);
    }

    @Path("/{id}")
    public AuthorResource getBookAuthor (@PathParam("id") String id) {
        return new AuthorResource();
    }
}
