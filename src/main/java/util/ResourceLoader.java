package util;


import annotations.*;
import entity.RequestHandler;
import io.netty.handler.codec.http.HttpMethod;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceLoader {

    public static List<RequestHandler> scan (String packagePath) {
        Reflections reflections = new Reflections(packagePath);
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(Path.class);

        return classSet
                .stream()
                .map(cls -> loadResource(cls, ""))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static List<RequestHandler> loadResource (Class<?> cls, String path) {
        List<RequestHandler> handlers = new ArrayList<>();

        try {
            Object obj = cls.newInstance();
            String rootPath = path + cls.getDeclaredAnnotation(Path.class).value();
            Method[] methods = cls.getDeclaredMethods();
            for (Method method : methods) {
                String pathPattern = MethodResolver.getPathPattern(rootPath, method);

                if (MethodResolver.isAnnotatedHttpMethod(method)) {
                    HttpMethod httpMethod = MethodResolver.getHttpMethod(method);
                    RequestHandler handler = new RequestHandler(obj, method, httpMethod, pathPattern);
                    handlers.add(handler);
                } else if (MethodResolver.isRequestSubResource(method)) {
                    List<RequestHandler> subHandlers = loadResource(method.getReturnType(), pathPattern);
                    handlers.addAll(subHandlers);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return handlers;
    }

}
