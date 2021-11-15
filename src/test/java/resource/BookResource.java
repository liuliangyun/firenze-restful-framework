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
    public String getBook (@PathParam("id") String id) {
        return "getBook: " + id;
    }

    @POST
    public MockRequestBody addBook (@RequestBody MockRequestBody body) {
        return body;
    }

    @DELETE
    @Path("/{id}")
    public String deleteBook (@PathParam("id") String id) {
        return "deleteBook: " + id;
    }

    @PUT
    @Path("/{id}")
    public MockRequestBody updateBook (@PathParam("id") String id, @RequestBody MockRequestBody body) {
        return body;
    }
}
