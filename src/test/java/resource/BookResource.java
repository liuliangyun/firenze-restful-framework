package resource;

import annotations.*;

@Path("/book")
public class BookResource {

    @GET
    public String getAllBooks () {
        return "getAllBooks";
    }

    @GET
    @Path("/{id}")
    public String getBookByIdWithPathParam (@PathParam("id") String id) {
        return "getBook: " + id;
    }

    @POST
    public String addBook (@RequestBody String body) {
        return "addBook: " + body;
    }

    @DELETE
    @Path("/{id}")
    public String deleteBook (@PathParam("id") String id) {
        return "deleteBook: " + id;
    }

    @PUT
    @Path("/{id}")
    public String updateBook (@PathParam("id") String id) {
        return "updateBook: " + id;
    }
}
