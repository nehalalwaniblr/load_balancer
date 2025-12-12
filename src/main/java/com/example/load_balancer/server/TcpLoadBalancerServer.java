package com.example.load_balancer.server;

import com.example.load_balancer.config.LoadBalancerConfig;
import com.example.load_balancer.strategies.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class TcpLoadBalancerServer {

    @Autowired
    LoadBalancerConfig config;
    @Autowired
    RoundRobinStrategy roundRobin;
    @Autowired
    WeightedRoundRobinStrategy weighted;
    @Autowired
    LeastConnectionsStrategy leastConn;
    @Autowired
    ConsistentHashingStrategy ch;

    @PostConstruct
    public void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(9000);

        new Thread(() -> {
            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(() -> handleClient(clientSocket)).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleClient(Socket clientSocket) {
        try {
            String selectedBackend = selectServer(clientSocket);

            String[] parts = selectedBackend.split(":");
            Socket backend = new Socket(parts[0], Integer.parseInt(parts[1]));

            forwardTraffic(clientSocket, backend);
            forwardTraffic(backend, clientSocket);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String selectServer(Socket clientSocket) {
        Strategy strategy = Strategy.valueOf(config.getStrategy());

        return switch (strategy) {
            case ROUND_ROBIN -> roundRobin.select(config.getBackendServers());
            case WEIGHTED_ROUND_ROBIN -> weighted.select();
            case LEAST_CONNECTIONS -> {
                String s = leastConn.select(config.getBackendServers());
                leastConn.increment(s);
                yield s;
            }
            case CONSISTENT_HASHING -> ch.select(clientSocket.getInetAddress().toString());
        };
    }

    private void forwardTraffic(Socket in, Socket out) throws Exception {
        new Thread(() -> {
            try (InputStream is = in.getInputStream();
                 OutputStream os = out.getOutputStream()) {

                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                    os.flush();
                }

            } catch (Exception ignored) {

            }
        }).start();
    }
}
