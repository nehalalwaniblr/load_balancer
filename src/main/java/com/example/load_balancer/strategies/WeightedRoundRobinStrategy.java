package com.example.load_balancer.strategies;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
@Component
public class WeightedRoundRobinStrategy {

    private final Map<String, Integer> weights = Map.of(
            "localhost:8085", 5,
            "localhost:8086", 1
    );

    private final List<String> expandedList = new ArrayList<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        weights.forEach((server, weight) -> {
            for (int i = 0; i < weight; i++)
                expandedList.add(server);
        });
    }

    public String select() {
        return expandedList.get(counter.getAndIncrement() % expandedList.size());
    }
}
