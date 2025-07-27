package com.example.application.bpe.service;

import com.example.application.common.channel.EventBroker;
import com.example.application.common.channel.EventChannel;
import com.example.application.common.container.App;
import com.example.application.common.model.UserInfo;
import com.example.application.common.model.command.CommandMessage;
import com.example.application.common.model.command.ResponseMessage;
import com.example.application.common.utils.JsonUtil;
import lombok.val;

@App.Bean
public class CommandService {

    private final EventChannel<String> bpeRsChannel;
    private final EventChannel<String> bpeRqChannel;

    public CommandService() {
        EventBroker eventBroker = App.getInstance(EventBroker.class);
        bpeRsChannel = eventBroker.getChannel("bpeRs");
        bpeRqChannel = eventBroker.getChannel("bpeRq");
        bpeRqChannel.addListener(message -> {
            CommandMessage cm = JsonUtil.jsonToObject(message, CommandMessage.class);
            execute(cm);
        });
    }

    public void execute(final CommandMessage cm) {
        if (cm.getCommand().equals("getUserInfo")) {
            val userInfo = new UserInfo();
            userInfo.setName("Test User");
            val rs = new ResponseMessage(
                    cm.getId(),
                    cm.getContext(),
                    null,
                    JsonUtil.objectToJsonNode(userInfo));
            String msg = JsonUtil.objectToJson(rs);
            bpeRsChannel.publish(msg);
        }
    }
}
