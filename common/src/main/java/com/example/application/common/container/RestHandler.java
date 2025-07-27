package com.example.application.common.container;

import com.example.application.common.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

/**
 * @author : Andrey Kolchev
 * @since : 05/05/2025
 */
@Slf4j
public class RestHandler implements HttpHandler {

    private static final String CONTENT_TYPE_JSON = "application/json";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "PUT":
                    handlePut(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    sendResponse(exchange, 405, "Method Not Allowed:");
            }
        } catch (RuntimeException e) {
            if (e instanceof IllegalArgumentException) {
                log.warn(e.getMessage());
                sendResponse(exchange, 400, "Bad Request: " + e.getMessage());
            } else if (e instanceof NoSuchElementException) {
                sendResponse(exchange, 404, "NotFound: " + e.getMessage());
            } else {
                sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendResponse(exchange, 500, "Unexpected Error: " + e.getMessage());
        }
    }

    protected void handleGet(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 404, "Not Found");
    }

    protected void handlePost(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 404, "Not Found");
    }

    protected void handlePut(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 404, "Not Found");
    }

    protected void handleDelete(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 404, "Not Found");
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    protected void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String response = JsonUtil.objectToJson(data);
        exchange.getResponseHeaders().set("Content-Type", CONTENT_TYPE_JSON);
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.sendResponseHeaders(statusCode, message.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(message.getBytes());
        }
    }
}
