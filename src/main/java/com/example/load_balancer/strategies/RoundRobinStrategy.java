package com.example.load_balancer.strategies;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RoundRobinStrategy {

    private final AtomicInteger counter = new AtomicInteger(0);

    public String select(List<String> servers) {
        int index = Math.abs(counter.getAndIncrement() % servers.size());
        return servers.get(index);
    }
}
