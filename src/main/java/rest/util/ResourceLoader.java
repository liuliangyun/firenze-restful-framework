package rest.util;


import cdi.Container;
import cdi.FirenzeContainer;
import rest.annotations.Path;
import rest.entity.MethodHandler;
import org.reflections.Reflections;
import rest.entity.RequestHandler;

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
                .map(cls -> loadResource(cls, "", null, null))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<RequestHandler> loadResource (Class<?> cls, String path, Object obj, List<MethodHandler> methodHandlers) {
        List<RequestHandler> requestHandlers = new ArrayList<>();

        try {
            if (obj == null) {
                obj = firenzeContainer.getComponent(cls);
            }
            if (methodHandlers == null) {
                methodHandlers = new ArrayList<>();
            }

            String clsRootPath = cls.getDeclaredAnnotation(Path.class).value();
            String fullPath = path + clsRootPath;
            Method[] methods = cls.getDeclaredMethods();
            for (Method method : methods) {
                List<MethodHandler> subMethodHandlers = new ArrayList<>();
                subMethodHandlers.addAll(methodHandlers);
                String methodPathPattern = MethodUtils.getPathPattern(clsRootPath, method);
                String fullPathPattern = MethodUtils.getPathPattern(fullPath, method);

                if (MethodUtils.isAnnotatedHttpMethod(method)) {
                    MethodHandler handler = new MethodHandler(method, methodPathPattern);
                    subMethodHandlers.add(handler);

                    RequestHandler requestHandler = new RequestHandler();
                    requestHandler.setMethodHandlers(subMethodHandlers);
                    requestHandler.setPathPattern(fullPathPattern);
                    requestHandler.setHttpMethod(MethodUtils.getHttpMethod(method));
                    requestHandler.setObj(obj);
                    requestHandlers.add(requestHandler);
                } else if (MethodUtils.isRequestSubResource(method)) {
                    MethodHandler handler = new MethodHandler(method, methodPathPattern);
                    subMethodHandlers.add(handler);

                    List<RequestHandler> subHandlers = loadResource(method.getReturnType(), fullPathPattern, obj, subMethodHandlers);
                    requestHandlers.addAll(subHandlers);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestHandlers;
    }

}
