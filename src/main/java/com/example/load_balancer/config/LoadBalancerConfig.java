package com.example.load_balancer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "lb")
@Data
public class LoadBalancerConfig {

    private List<String> backendServers;
    private Map<String, String> connectionMapping;
    private String strategy;

    // getters and setters
}
