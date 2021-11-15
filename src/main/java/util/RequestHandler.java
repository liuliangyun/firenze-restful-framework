package util;

import annotations.PathParam;
import annotations.RequestBody;
import entity.HttpMethod;
import entity.HttpRequest;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Data
public class RequestHandler {

    private Object obj;
    private Method resolver;
    private HttpMethod httpMethod;
    private String pathPattern;


    public String handling (HttpRequest request) throws InvocationTargetException, IllegalAccessException {
        if (resolver == null) {
            return null;
        }
        Parameter[] parameters = resolver.getParameters();
        Object[] pars = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            if (p.isAnnotationPresent(PathParam.class)) {
                String key = p.getDeclaredAnnotation(PathParam.class).value();
                String value = request.getParams().get(key);
                pars[i] = value;
            } else if (p.isAnnotationPresent(RequestBody.class)){
                pars[i] = request.getBody();
            } else {
                pars[i] = null;
            }
        }
        return (String) resolver.invoke(obj, pars);
    }

    public boolean isMatch (HttpRequest request) {
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
        for (int i = 0; i< pathPatternToken.length; i++) {
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
