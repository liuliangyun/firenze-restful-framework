package util;


import annotations.*;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;

public class ResourceLoader {


    public static List<RequestHandler> scan (String packagePath) {
        Reflections reflections = new Reflections(packagePath);
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(Path.class);

        List<RequestHandler> handlers = new ArrayList<>();
        for (Class<?> cls : classSet) {
            try {
                Object obj = cls.newInstance();
                String rootPath = cls.getDeclaredAnnotation(Path.class).value();
                Method[] methods = cls.getMethods();
                for (Method method : methods) {
                    if (MethodResolver.isAnnotatedHttpMethod(method)) {
                        RequestHandler handler = new RequestHandler();
                        handler.setObj(obj);
                        handler.setMethod(method);
                        handler.setHttpMethod(MethodResolver.getHttpMethod(method));
                        handler.setPathPattern(MethodResolver.getPathPattern(rootPath, method));
                        handlers.add(handler);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return handlers;
    }

}
