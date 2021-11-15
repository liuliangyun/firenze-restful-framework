package util;


import annotations.*;
import entity.HttpMethod;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;

public class ResourceLoader {
    private static final Map<Class, HttpMethod> ANNOTATION_HTTP_METHOD_MAP = new HashMap<Class, HttpMethod>() {
        {
            put(GET.class, HttpMethod.GET);
            put(POST.class, HttpMethod.POST);
            put(PUT.class, HttpMethod.PUT);
            put(DELETE.class, HttpMethod.DELETE);
        }
    };

    public static List<RequestHandler> scan (String packagePath) {
        Reflections reflections = new Reflections(packagePath);
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(Path.class);

        List<RequestHandler> handlers = new ArrayList<>();
        for (Class<?> cls : classSet) {
            try {
                Object obj = cls.newInstance();
                String rootPath = cls.getDeclaredAnnotation(Path.class).value();
                // 获取所有暴露的方法
                Method[] methods = cls.getMethods();
                for (Method method : methods) {
                    if (isAnnotatedHttpMethod(method)) {
                        RequestHandler handler = new RequestHandler();
                        handler.setObj(obj);
                        handler.setResolver(method);
                        handler.setPathPattern(resolvePathAnnotation(rootPath, method));
                        handler.setHttpMethod(resolveHttpMethodAnnotation(method));
                        handlers.add(handler);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return handlers;
    }

    private static boolean isAnnotatedHttpMethod(Method method) {
        return ANNOTATION_HTTP_METHOD_MAP.keySet().stream().anyMatch(method::isAnnotationPresent);
    }

    private static String resolvePathAnnotation(String rootPath, Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(rootPath);
        if (method.isAnnotationPresent(Path.class)) {
            String childPath = method.getDeclaredAnnotation(Path.class).value();
            sb.append(childPath);
        }
        return sb.toString();
    }

    private static HttpMethod resolveHttpMethodAnnotation (Method method) {
        Optional<Class> annotation = ANNOTATION_HTTP_METHOD_MAP.keySet().stream().filter(method::isAnnotationPresent).findAny();
        return annotation.map(ANNOTATION_HTTP_METHOD_MAP::get).orElse(null);
    }

}
