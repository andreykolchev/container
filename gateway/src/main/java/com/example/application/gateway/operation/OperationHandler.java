package com.example.application.gateway.operation;

import com.example.application.common.container.App;
import com.example.application.common.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Slf4j
@App.Path("/api/execute-operation")
public class OperationHandler implements HttpHandler {

    private final OperationService operationService;

    public OperationHandler() {
        this.operationService = App.getInstance(OperationService.class);;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        val operationId = UUID.randomUUID().toString();
        String command = "getUserInfo";
        val rs = operationService.executeOperation(operationId, command);
        val response = JsonUtil.objectToJson(rs);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}