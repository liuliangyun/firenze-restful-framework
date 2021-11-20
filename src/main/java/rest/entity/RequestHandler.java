package rest.entity;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Data;
import rest.util.PathUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Data
public class RequestHandler {

    private List<MethodHandler> methodHandlers;
    private String pathPattern;
    private HttpMethod httpMethod;

    // 起始资源的实例
    private Object obj;

    public Object handling (RestfulRequest request) throws InvocationTargetException, IllegalAccessException {
        Object instance = obj;
        for (MethodHandler handler: methodHandlers) {
            instance = handler.handling(request, instance);
            if (instance == null) {
                break;
            }
        }
        return instance;
    }


    public boolean isMatch (RestfulRequest request) {
        return matchHttpMethod(request.getMethod()) && matchPathPattern(request.getPath());
    }

    private boolean matchHttpMethod (HttpMethod method) {
        return httpMethod == method;
    }

    private boolean matchPathPattern(String url) {
        List<String> urlToken = PathUtils.getPathToken(url);
        List<String> pathPatternToken = PathUtils.getPathToken(pathPattern);
        if (urlToken.size() != pathPatternToken.size()) {
            return false;
        }
        for (int i = 0; i < pathPatternToken.size(); i++) {
            if (pathPatternToken.get(i).matches("\\{(.*)\\}")) {
                continue;
            }
            if (!pathPatternToken.get(i).equals(urlToken.get(i))) {
                return false;
            }
        }
        return true;
    }

}
