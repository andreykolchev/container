package com.example.application.common.container;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * @author : Andrey Kolchev
 * @since : 05/05/2025
 */
@Slf4j
public class App {

    private static HttpServer server;
    private static final Map<Class<?>, Object> SINGLETONS = new HashMap<>();

    public static void createServer(int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        } catch (IOException e) {
            log.error("Failed to create HTTP server on port {}", port, e);
            throw new IllegalStateException("Failed to create server", e);
        }
    }

    public static void startServer() {
        if (server == null) {
            log.error("Server is not initialized. Call createServer first.");
            throw new IllegalStateException("Server is not initialized.");
        }
        server.start();
        log.info("Server started on port: {}", server.getAddress().getPort());
    }

    public static void registerBeans(Set<String> packages) {
        for (Class<?> clazz : findAllBeansClasses(packages)) {
            if (clazz.isAnnotationPresent(Bean.class)) {
                registerBean(clazz);
            }
        }
    }

    public static void registerHandlers(Set<String> packages) {
        for (Class<?> clazz : findAllHandlerClasses(packages)) {
            if (clazz.isAnnotationPresent(Path.class)) {
                registerHandler(clazz);
            }
        }
    }

    private static <T> void registerBean(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            SINGLETONS.put(clazz, instance);
            log.info("Registered bean: {}", clazz.getName());
        } catch (ReflectiveOperationException e) {
            log.error("Failed to register bean: {}", clazz.getName(), e);
            throw new IllegalStateException("Failed to register bean: " + clazz.getName(), e);
        }
    }

    private static void registerHandler(Class<?> clazz) {
        try {
            if (!(HttpHandler.class.isAssignableFrom(clazz))) {
                throw new IllegalArgumentException("Class " + clazz.getName() + " does not implement HttpHandler");
            }
            HttpHandler handlerInstance = (HttpHandler) clazz.getDeclaredConstructor().newInstance();
            String path = clazz.getAnnotation(Path.class).value();
            server.createContext(path, handlerInstance);
            log.info("Registered handler for path: {}", path);
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            log.error("Failed to register handler: {}", clazz.getName(), e);
        }
    }

    private static Set<Class<?>> findAllBeansClasses(Set<String> packages) {
        Set<Class<?>> classes = new HashSet<>();
        for (String pkg : packages) {
            Reflections reflections = new Reflections(pkg, Scanners.SubTypes.filterResultsBy(c -> true));
            classes.addAll(reflections.getSubTypesOf(Object.class));
        }
        return classes;
    }

    private static Set<Class<?>> findAllHandlerClasses(Set<String> packages) {
        Set<Class<?>> classes = new HashSet<>();
        for (String pkg : packages) {
            Reflections reflections = new Reflections(pkg, Scanners.SubTypes.filterResultsBy(c -> true));
            classes.addAll(reflections.getSubTypesOf(Object.class));
            classes.addAll(reflections.getSubTypesOf(RestHandler.class));
        }
        return classes;
    }

    public static <T> T getInstance(Class<T> clazz) {
        if (SINGLETONS.get(clazz) == null) {
            registerBean(clazz);
        }
        return clazz.cast(SINGLETONS.get(clazz));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface Bean {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Path {
        String value();
    }
}
