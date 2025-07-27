package com.example.application.common.channel;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * @author : Andrey Kolchev
 * @since : 05/05/2025
 */
public class EventChannel<T> {

    private final String name;
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();
    private final List<Consumer<T>> listeners = new CopyOnWriteArrayList<>();
    private volatile boolean running = true;

    public EventChannel(String name) {
        this.name = name;
        // Start consumer thread
        Thread.startVirtualThread(this::processMessages);
    }

    // Add a new listener
    public void addListener(Consumer<T> listener) {
        listeners.add(listener);
    }

    // Publish an event (adds to the queue)
    public void publish(T message) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Process events in a separate thread
    private void processMessages() {
        try {
            while (running) {
                T message = queue.take(); // Blocking until an event is available
                for (Consumer<T> listener : listeners) {
                    listener.accept(message);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Graceful shutdown
    public void stop() {
        running = false;
    }

    public String getName() {
        return name;
    }
}