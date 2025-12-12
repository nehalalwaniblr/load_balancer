package com.example.load_balancer.strategies;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

@Component
public class ConsistentHashingStrategy {

    private final SortedMap<Integer, String> ring = new TreeMap<>();

    @PostConstruct
    public void init() {
        List<String> servers = List.of("localhost:8085", "localhost:8085");
        for (String server : servers) {
            int hash = server.hashCode();
            ring.put(hash, server);
        }
    }

    public String select(String clientKey) {
        int hash = clientKey.hashCode();
        SortedMap<Integer, String> tail = ring.tailMap(hash);
        return tail.isEmpty() ? ring.get(ring.firstKey()) : ring.get(tail.firstKey());
    }
}
