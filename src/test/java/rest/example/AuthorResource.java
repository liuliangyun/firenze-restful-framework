package rest.example;

import rest.annotations.*;

@Path("/author")
public class AuthorResource {

    @GET
    public String getAllAuthors() {
        return "getAllAuthors";
    }

    @GET
    @Path("/{id}")
    public String getAuthor (@PathParam("id") String id) {
        return "getAuthor: " + id;
    }

    @POST
    public Author addAuthor (@RequestBody Author body) {
        return body;
    }
}
