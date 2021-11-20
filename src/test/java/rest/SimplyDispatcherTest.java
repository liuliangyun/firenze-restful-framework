package rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import rest.dispatcher.DispatcherConfig;
import rest.dispatcher.SimplyDispatcher;
import rest.entity.RestfulResponse;
import rest.example.Author;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rest.example.Book;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimplyDispatcherTest {
    DispatcherConfig dispatcherConfig = new DispatcherConfig("rest.example");
    SimplyDispatcher simplyDispatcher;

    @BeforeEach
    public void setUp () {
        simplyDispatcher = new SimplyDispatcher(dispatcherConfig);
        simplyDispatcher.init();
    }

    @Test
    public void should_init_request_handler_mapping () {
        assertEquals(11, simplyDispatcher.getRequestHandlerMapping().getRequestHandlers().size());
    }

    @Test
    public void should_get_bad_request_when_request_url_is_not_found () {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/not-found-path");
        RestfulResponse response = simplyDispatcher.dispatch(httpRequest);
        assertEquals(HttpResponseStatus.BAD_REQUEST, response.getStatus());
        assertEquals(null, response.getBody());
    }

    @Test
    public void should_get_OK_when_request_url_is_found () {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/book");
        RestfulResponse response = simplyDispatcher.dispatch(httpRequest);
        assertEquals(HttpResponseStatus.OK, response.getStatus());
        assertEquals("getAllBooks", response.getBody());
    }

    @Test
    public void should_get_ok_when_request_url_with_path_param () {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/book/1");
        RestfulResponse response = simplyDispatcher.dispatch(httpRequest);
        assertEquals(HttpResponseStatus.OK, response.getStatus());
        assertEquals("getBook: 1", response.getBody());
    }

    @Test
    public void should_post_OK_when_request_url_with_request_body () {
        Book body = new Book("ruby");
        byte[] bytes = JSON.toJSONBytes(body, SerializerFeature.EMPTY);
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.POST, "/book", Unpooled.wrappedBuffer(bytes));
        RestfulResponse response = simplyDispatcher.dispatch(httpRequest);
        assertEquals(HttpResponseStatus.OK, response.getStatus());
        Book responseBody = (Book) response.getBody();
        assertEquals("ruby", responseBody.getName());
    }

    @Test
    public void should_delete_OK_when_request_url_with_path_param () {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.DELETE, "/book/1");
        RestfulResponse response = simplyDispatcher.dispatch(httpRequest);
        assertEquals(HttpResponseStatus.OK, response.getStatus());
        assertEquals("deleteBook: 1", response.getBody());
    }

    @Test
    public void should_update_OK_when_request_url_with_path_param_and_request_body () {
        Book body = new Book("ruby");
        byte[] bytes = JSON.toJSONBytes(body, SerializerFeature.EMPTY);
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.PUT, "/book/1", Unpooled.wrappedBuffer(bytes));
        RestfulResponse response = simplyDispatcher.dispatch(httpRequest);
        assertEquals(HttpResponseStatus.OK, response.getStatus());
        Book responseBody = (Book) response.getBody();
        assertEquals("ruby", responseBody.getName());
    }

    @Test
    public void should_get_OK_when_request_sub_resource () {
        FullHttpRequest httpRequest1 = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/book/1/author");
        FullHttpRequest httpRequest2 = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/author");
        RestfulResponse response1 = simplyDispatcher.dispatch(httpRequest1);
        RestfulResponse response2 = simplyDispatcher.dispatch(httpRequest2);
        assertEquals(HttpResponseStatus.OK, response1.getStatus());
        assertEquals(HttpResponseStatus.OK, response2.getStatus());
        assertEquals("getBookAuthors", response1.getBody());
        assertEquals("getAllAuthors", response2.getBody());
    }

    @Test
    public void should_get_OK_when_request_sub_resource_with_path_param () {
        FullHttpRequest httpRequest1 = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/book/1/author/2");
        FullHttpRequest httpRequest2 = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/author/2");
        RestfulResponse response1 = simplyDispatcher.dispatch(httpRequest1);
        RestfulResponse response2 = simplyDispatcher.dispatch(httpRequest2);
        assertEquals(HttpResponseStatus.OK, response1.getStatus());
        assertEquals(HttpResponseStatus.OK, response2.getStatus());
        assertEquals("getBookAuthor: 2", response1.getBody());
        assertEquals("getAuthor: 2", response2.getBody());
    }

    @Test
    public void should_post_OK_when_request_sub_resource_with_request_body () {
        Author body = new Author("xxx");
        byte[] bytes = JSON.toJSONBytes(body, SerializerFeature.EMPTY);
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.POST, "/book/1/author", Unpooled.wrappedBuffer(bytes));
        RestfulResponse response = simplyDispatcher.dispatch(httpRequest);
        assertEquals(HttpResponseStatus.OK, response.getStatus());
        Author responseBody = (Author) response.getBody();
        assertEquals("xxx", responseBody.getName());
    }
}
