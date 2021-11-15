import dispatcher.DispatcherConfig;
import dispatcher.SimplyDispatcher;
import entity.HttpMethod;
import entity.HttpRequest;
import entity.HttpResponse;
import entity.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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
        HttpRequest request = new HttpRequest(HttpMethod.GET, "/not-found-path", null, "");
        HttpResponse response = simplyDispatcher.dispatch(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals(null, response.getBody());
    }

    @Test
    public void should_get_OK_when_request_url_is_found () {
        HttpRequest request = new HttpRequest(HttpMethod.GET, "/book", null, "");
        HttpResponse response = simplyDispatcher.dispatch(request);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("getAllBooks", response.getBody());
    }

    @Test
    public void should_get_ok_when_request_url_with_path_param () {
        Map<String, String> params = new HashMap<>();
        params.put("id", "1");
        HttpRequest request = new HttpRequest(HttpMethod.GET, "/book/1", params, "");
        HttpResponse response = simplyDispatcher.dispatch(request);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("getBook: 1", response.getBody());
    }

    @Test
    public void should_post_OK_when_request_url_with_request_body () {
        HttpRequest request = new HttpRequest(HttpMethod.POST, "/book", null, "ruby");
        HttpResponse response = simplyDispatcher.dispatch(request);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("addBook: ruby", response.getBody());
    }

    @Test
    public void should_delete_OK_when_request_url_with_path_param () {
        Map<String, String> params = new HashMap<>();
        params.put("id", "1");
        HttpRequest request = new HttpRequest(HttpMethod.DELETE, "/book/1", params, "");
        HttpResponse response = simplyDispatcher.dispatch(request);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("deleteBook: 1", response.getBody());
    }

    @Test
    public void should_update_OK_when_request_url_with_path_param () {
        Map<String, String> params = new HashMap<>();
        params.put("id", "1");
        HttpRequest request = new HttpRequest(HttpMethod.PUT, "/book/1", params, "");
        HttpResponse response = simplyDispatcher.dispatch(request);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("updateBook: 1", response.getBody());
    }
}
