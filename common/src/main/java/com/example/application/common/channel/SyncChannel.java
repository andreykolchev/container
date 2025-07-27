package com.example.application.common.channel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author : Andrey Kolchev
 * @since : 05/05/2025
 */
public class SyncChannel<K, V> {

    private final ConcurrentHashMap<K, CompletableFuture<V>> syncChannel;

    public SyncChannel() {
        syncChannel = new ConcurrentHashMap<>();
    }

    private CompletableFuture<V> getSource(K key) {
        return syncChannel.computeIfAbsent(key, k -> new CompletableFuture<>());
    }

    public V getValue(K key) {
        try {
            V value = getSource(key).get(30, TimeUnit.SECONDS);
            syncChannel.remove(key);
            return value;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while getting value", e);
        }
    }

    public void putValue(K key, V value) {
        getSource(key).complete(value);
    }
}
