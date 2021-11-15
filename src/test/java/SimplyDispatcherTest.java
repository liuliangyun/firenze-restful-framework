import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import dispatcher.DispatcherConfig;
import dispatcher.SimplyDispatcher;
import entity.RestfulResponse;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resource.MockRequestBody;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimplyDispatcherTest {
    DispatcherConfig dispatcherConfig = new DispatcherConfig("resource");
    SimplyDispatcher simplyDispatcher;

    @BeforeEach
    public void setUp () {
        simplyDispatcher = new SimplyDispatcher(dispatcherConfig);
        simplyDispatcher.init();
    }

    @Test
    public void should_init_request_handler_mapping () {
        assertEquals(5, simplyDispatcher.getRequestHandlerMapping().getHandlers().size());
    }

    @Test
    public void should_get_NOT_FOUND_when_request_url_is_not_found () {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/not-found-path");
        RestfulResponse response = simplyDispatcher.dispatch(httpRequest);
        assertEquals(HttpResponseStatus.NOT_FOUND, response.getStatus());
        assertEquals(null, response.getBody());
    }

    @Test
    public void should_get_OK_when_request_url_is_found () {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/book");
        RestfulResponse response = simplyDispatcher.dispatch(httpRequest);
        assertEquals(HttpResponseStatus.OK, response.getStatus());
        assertEquals("getAllBooks", response.getBody());
    }

    @Test
    public void should_get_ok_when_request_url_with_path_param () {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/book/1");
        RestfulResponse response = simplyDispatcher.dispatch(httpRequest);
        assertEquals(HttpResponseStatus.OK, response.getStatus());
        assertEquals("getBook: 1", response.getBody());
    }

    @Test
    public void should_post_OK_when_request_url_with_request_body () {
        MockRequestBody body = new MockRequestBody("ruby");
        byte[] bytes = JSON.toJSONBytes(body, SerializerFeature.EMPTY);
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/book", Unpooled.wrappedBuffer(bytes));
        RestfulResponse response = simplyDispatcher.dispatch(httpRequest);
        assertEquals(HttpResponseStatus.OK, response.getStatus());
        MockRequestBody responseBody = (MockRequestBody) response.getBody();
        assertEquals("ruby", responseBody.getName());
    }

    @Test
    public void should_delete_OK_when_request_url_with_path_param () {
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.DELETE, "/book/1");
        RestfulResponse response = simplyDispatcher.dispatch(httpRequest);
        assertEquals(HttpResponseStatus.OK, response.getStatus());
        assertEquals("deleteBook: 1", response.getBody());
    }

    @Test
    public void should_update_OK_when_request_url_with_path_param_and_request_body () {
        MockRequestBody body = new MockRequestBody("ruby");
        byte[] bytes = JSON.toJSONBytes(body, SerializerFeature.EMPTY);
        FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, "/book/1", Unpooled.wrappedBuffer(bytes));
        RestfulResponse response = simplyDispatcher.dispatch(httpRequest);
        assertEquals(HttpResponseStatus.OK, response.getStatus());
        MockRequestBody responseBody = (MockRequestBody) response.getBody();
        assertEquals("ruby", responseBody.getName());
    }
}
