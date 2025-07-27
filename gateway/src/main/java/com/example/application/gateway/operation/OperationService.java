package com.example.application.gateway.operation;

import com.example.application.common.channel.EventBroker;
import com.example.application.common.channel.EventChannel;
import com.example.application.common.channel.SyncChannel;
import com.example.application.common.container.App;
import com.example.application.common.model.UserInfo;
import com.example.application.common.model.command.CommandMessage;
import com.example.application.common.model.command.ResponseMessage;
import com.example.application.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashMap;

@Slf4j
@App.Bean
public class OperationService {

    private final EventChannel<String> bpeRqChannel;
    private final EventChannel<String> bpeRsChannel;
    private final SyncChannel<String, ResponseMessage> syncChannel;

    public OperationService() {
        val eventBroker = App.getInstance(EventBroker.class);
        syncChannel = new SyncChannel<>();
        bpeRqChannel = eventBroker.getChannel("bpeRq");
        bpeRsChannel = eventBroker.getChannel("bpeRs");
        bpeRsChannel.addListener(message -> {
            ResponseMessage responseMessage = JsonUtil.jsonToObject(message, ResponseMessage.class);
            syncChannel.putValue(responseMessage.getId(), responseMessage);
        });
    }

    public UserInfo executeOperation(String operationId, String command) {
        //send command
        final HashMap<String, String> params = new HashMap<>();
        val context = JsonUtil.objectToJsonNode(params);
        val data = JsonUtil.empty();
        val commandMessage = new CommandMessage(operationId, command, context, data);
        bpeRqChannel.publish(JsonUtil.objectToJson(commandMessage));
        //wait for response
        val responseMessage = syncChannel.getValue(operationId);
        if (responseMessage != null) {
            if (responseMessage.getError() == null) {
                return JsonUtil.jsonNodeToObject(responseMessage.getData(), UserInfo.class);
            } else {
                log.warn("Error in response from bpe service: operationId: {}, command: {}, error: {}", operationId, command, responseMessage.getError().getMessage());
                throw new RuntimeException(responseMessage.getError().getMessage());
            }
        } else {
            log.warn("No response from bpe service: operationId: {}, command: {} ", operationId, command);
            throw new RuntimeException("No response from bpe service");
        }
    }

//    public UserInfo executeOperation(String operationId, String command) {
//        val rs =  new UserInfo(); rs.setFullName("Test User");
//        return rs;
//    }
}
