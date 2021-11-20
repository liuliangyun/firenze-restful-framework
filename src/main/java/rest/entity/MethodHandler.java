package rest.entity;

import lombok.AllArgsConstructor;
import rest.annotations.PathParam;
import rest.annotations.RequestBody;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import rest.util.PathUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Data
@AllArgsConstructor
public class MethodHandler {

    private Method method;
    private String pathPattern;


    public Object handling (RestfulRequest request, Object obj) throws InvocationTargetException, IllegalAccessException {
        if (method == null) {
            return null;
        }

        Map<String, String> paramMap = parseParamsFromRequestPath(request.getPath());
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            if (p.isAnnotationPresent(PathParam.class)) {
                String key = p.getDeclaredAnnotation(PathParam.class).value();
                params[i] = paramMap.get(key);
            } else if (p.isAnnotationPresent(RequestBody.class)){
                params[i] = JSON.parseObject(request.getBody(), p.getType());
            } else {
                params[i] = null;
            }
        }
        return method.invoke(obj, params);
    }

    private Map<String, String> parseParamsFromRequestPath(String requestPath) {
        Map<String, String> paramMap = new HashMap<>();
        List<String> urlToken = PathUtils.getPathToken(requestPath);
        List<String> pathPatternToken = PathUtils.getPathToken(pathPattern);
        List<String> matchedUrlToken = getMatchedUrlToken(urlToken, pathPatternToken.get(0));

        for (int i = 0; i < pathPatternToken.size(); i++) {
            if (pathPatternToken.get(i).matches("\\{(.*)\\}")) {
                String key = pathPatternToken.get(i).substring(1, pathPatternToken.get(i).length() - 1);
                paramMap.put(key, matchedUrlToken.get(i));
            }
        }
        return paramMap;
    }

    private List<String> getMatchedUrlToken (List<String> urlToken, String rootPath) {
        for (int i = 0; i < urlToken.size(); i++) {
            if (urlToken.get(i).equals(rootPath)) {
                return urlToken.subList(i, urlToken.size());
            }
        }
        return new ArrayList<>();
    }

}
