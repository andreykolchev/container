package com.example.application.common.channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : Andrey Kolchev
 * @since : 05/05/2025
 */
public class EventBroker {

    private final ConcurrentHashMap<String, EventChannel<?>> channels;

    public EventBroker() {
        channels = new ConcurrentHashMap<>();
    }

    public <T> EventChannel<T> getChannel(String name) {
        return (EventChannel<T>) channels.computeIfAbsent(name, EventChannel::new);
    }
}
