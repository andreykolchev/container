package com.example.application.gateway.user;

import com.example.application.common.container.App;
import com.example.application.common.container.RestHandler;
import com.example.application.common.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.example.application.common.container.App.getInstance;

@Slf4j
@App.Path("/api/users")
public class UserHandler extends RestHandler {

    private final UserService userService;

    public UserHandler() {
        this.userService = getInstance(UserService.class);
    }

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        String id = extractId(exchange);
        if (id != null && !id.isEmpty()) {
            sendJsonResponse(exchange, 200, userService.getUserById(id));
        } else {
            sendJsonResponse(exchange, 200, userService.getAllUsers());
        }
    }

    @Override
    public void handlePost(HttpExchange exchange) throws IOException {
        User user = JsonUtil.jsonToObject(readRequestBody(exchange), User.class);
        User createdUser = userService.createUser(user);
        sendJsonResponse(exchange, 201, createdUser);
    }

    @Override
    public void handlePut(HttpExchange exchange) throws IOException {
        String id = getId(exchange);
        User body = JsonUtil.jsonToObject(readRequestBody(exchange), User.class);
        User updatedUser = userService.updateUser(id, body);
        sendJsonResponse(exchange, 200, updatedUser);
    }

    @Override
    public void handleDelete(HttpExchange exchange) throws IOException {
        String id = getId(exchange);
        userService.deleteUser(id);
        sendResponse(exchange, 204, "ok"); // No Content
    }

    private String extractId(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        return (parts.length == 4) ? parts[3] : null;  // /api/users/{id}
    }

    private String getId(HttpExchange exchange) throws IOException {
        String id = extractId(exchange);
        if (id == null || id.isEmpty()) {
            throw  new IllegalArgumentException("ID is required.");
        }
        return id;
    }
}

