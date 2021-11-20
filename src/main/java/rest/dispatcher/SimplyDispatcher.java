package rest.dispatcher;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;
import rest.entity.*;
import rest.util.ResourceLoader;

import java.lang.reflect.InvocationTargetException;

@Data
public class SimplyDispatcher implements Dispatcher{
    private DispatcherConfig dispatcherConfig;
    private RequestHandlerMapping requestHandlerMapping;

    public SimplyDispatcher(DispatcherConfig dispatcherConfig) {
        this.dispatcherConfig = dispatcherConfig;
    }

    @Override
    public void init() {
        String resourcePath = dispatcherConfig.getResourcePath();
        ResourceLoader resourceLoader = new ResourceLoader();
        requestHandlerMapping = new RequestHandlerMapping(resourceLoader.scan(resourcePath));
    }

    @Override
    public RestfulResponse dispatch(FullHttpRequest httpRequest) {
        RestfulRequest request = RestfulRequest.of(httpRequest);
        RestfulResponse response = new RestfulResponse();
        try {
            RequestHandler requestHandler = requestHandlerMapping.mapping(request);
            Object responseData = requestHandler.handling(request);
            response.setBody(responseData);
            if (responseData == null) {
                response.setStatus(HttpResponseStatus.NOT_FOUND);
            } else{
                response.setStatus(HttpResponseStatus.OK);
            }
        } catch (NullPointerException e) {
          response.setStatus(HttpResponseStatus.BAD_REQUEST);
          e.printStackTrace();
        } catch (InvocationTargetException e) {
            response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        return response;
    }
}
