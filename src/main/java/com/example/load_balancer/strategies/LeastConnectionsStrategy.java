package com.example.load_balancer.strategies;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LeastConnectionsStrategy {

    private final Map<String, AtomicInteger> activeConnections = new ConcurrentHashMap<>();

    public String select(List<String> servers) {
        servers.forEach(s -> activeConnections.putIfAbsent(s, new AtomicInteger(0)));
        return servers.stream()
                .min(Comparator.comparingInt(s -> activeConnections.get(s).get()))
                .orElseThrow();
    }

    public void increment(String server) {
        activeConnections.get(server).incrementAndGet();
    }

    public void decrement(String server) {
        activeConnections.get(server).decrementAndGet();
    }
}
