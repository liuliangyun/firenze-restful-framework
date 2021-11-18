package rest.util;

import io.netty.handler.codec.http.HttpMethod;
import rest.annotations.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MethodResolver {
    private static final Map<Class, HttpMethod> ANNOTATION_HTTP_METHOD_MAP = new HashMap<Class, HttpMethod>() {
        {
            put(GET.class, HttpMethod.GET);
            put(POST.class, HttpMethod.POST);
            put(PUT.class, HttpMethod.PUT);
            put(DELETE.class, HttpMethod.DELETE);
        }
    };

    public static boolean isAnnotatedHttpMethod(Method method) {
        return ANNOTATION_HTTP_METHOD_MAP.keySet().stream().anyMatch(method::isAnnotationPresent);
    }

    public static HttpMethod getHttpMethod (Method method) {
        Optional<Class> annotation = ANNOTATION_HTTP_METHOD_MAP.keySet().stream().filter(method::isAnnotationPresent).findAny();
        return annotation.map(ANNOTATION_HTTP_METHOD_MAP::get).orElse(null);
    }

    public static String getPathPattern (String rootPath, Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(rootPath);
        if (method.isAnnotationPresent(Path.class)) {
            String childPath = method.getDeclaredAnnotation(Path.class).value();
            sb.append(childPath);
        }
        return sb.toString();
    }

    public static boolean isRequestSubResource(Method method) {
        return method.getReturnType().isAnnotationPresent(Path.class);
    }
}
