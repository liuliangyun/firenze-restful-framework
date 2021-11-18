package rest.entity;

import rest.annotations.PathParam;
import rest.annotations.RequestBody;
import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class RequestHandler {

    private Object obj;
    private Method method;
    private HttpMethod httpMethod;
    private String pathPattern;


    public Object handling (RestfulRequest request) throws InvocationTargetException, IllegalAccessException {
        if (method == null) {
            return null;
        }
        Map<String, String> pathParams = parseRequest(request);

        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            if (p.isAnnotationPresent(PathParam.class)) {
                String key = p.getDeclaredAnnotation(PathParam.class).value();
                params[i] = pathParams.get(key);
            } else if (p.isAnnotationPresent(RequestBody.class)){
                params[i] = JSON.parseObject(request.getBody(), p.getType());
            } else {
                params[i] = null;
            }
        }
        return method.invoke(obj, params);
    }

    private Map<String, String> parseRequest(RestfulRequest request) {
        Map<String, String> pathParams = new HashMap<>();
        String[] urlToken = request.getPath().split("/");
        String[] pathPatternToken = pathPattern.split("/");
        for (int i = 0; i < pathPatternToken.length; i++) {
            if (pathPatternToken[i].matches("\\{(.*)\\}")) {
                String key = pathPatternToken[i].substring(1, pathPatternToken[i].length() - 1);
                pathParams.put(key, urlToken[i]);
            }
        }
        return pathParams;
    }

    public boolean isMatch (RestfulRequest request) {
        return matchHttpMethod(request.getMethod()) && matchPathPattern(request.getPath());
    }

    private boolean matchHttpMethod (HttpMethod method) {
        return httpMethod == method;
    }

    private boolean matchPathPattern(String url) {
        String[] urlToken = url.split("/");
        String[] pathPatternToken = pathPattern.split("/");
        if (urlToken.length != pathPatternToken.length) {
            return false;
        }
        for (int i = 0; i < pathPatternToken.length; i++) {
            if (pathPatternToken[i].matches("\\{(.*)\\}")) {
                continue;
            }
            if (!pathPatternToken[i].equals(urlToken[i])) {
                return false;
            }
        }
        return true;
    }

}
