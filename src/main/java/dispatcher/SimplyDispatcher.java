package dispatcher;

import entity.*;
import lombok.Data;
import util.ResourceLoader;
import util.RequestHandler;
import util.RequestHandlerMapping;

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
        requestHandlerMapping = new RequestHandlerMapping(ResourceLoader.scan(resourcePath));
    }

    @Override
    public HttpResponse dispatch(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        try {
            RequestHandler handler = requestHandlerMapping.mapping(request);
            String responseData = handler.handling(request);
            response.setBody(responseData);
            response.setStatus(HttpStatus.OK);
        } catch (NullPointerException e) {
          response.setStatus(HttpStatus.NOT_FOUND);
          e.printStackTrace();
        } catch (InvocationTargetException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        return response;
    }
}
