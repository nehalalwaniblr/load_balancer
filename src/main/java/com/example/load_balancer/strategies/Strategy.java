package com.example.load_balancer.strategies;

public enum Strategy {
    ROUND_ROBIN,
    WEIGHTED_ROUND_ROBIN,
    LEAST_CONNECTIONS,
    CONSISTENT_HASHING
}
