package rest.util;


import cdi.Container;
import cdi.FirenzeContainer;
import rest.annotations.Path;
import rest.entity.RequestHandler;
import io.netty.handler.codec.http.HttpMethod;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceLoader {
    private final Container firenzeContainer;

    public ResourceLoader() {
        firenzeContainer = new FirenzeContainer();
    }

    public List<RequestHandler> scan (String packagePath) {
        Reflections reflections = new Reflections(packagePath);
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(Path.class);

        return classSet
                .stream()
                .map(cls -> loadResource(cls, ""))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<RequestHandler> loadResource (Class<?> cls, String path) {
        List<RequestHandler> handlers = new ArrayList<>();

        try {
            Object obj = firenzeContainer.getComponent(cls);
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
