Below is a **clean, professional, GitHub-ready README.md** for your project, including:

✔ Overview
✔ Architecture diagram (ASCII)
✔ Why the **other service (hello-lb)** is needed
✔ How to run
✔ How each LB strategy works
✔ Testing instructions (curl, Postman, nc)
✔ Limitations & next steps

You can paste this directly into your GitHub repo.

---

# 📘 **README.md — TCP Load Balancer (Spring Boot + Java)**

## 🚀 Overview

This project implements a **custom TCP Load Balancer** using **pure Java sockets** and **Spring Boot configuration management**.
It demonstrates how a load balancer can:

* Accept TCP connections from clients
* Forward raw TCP traffic to backend servers
* Route requests using multiple load-balancing strategies
* Maintain per-connection routing like NGINX stream/TCP LB
* Support connection-mapping between LB connection ids and backend connection ids

This LB is built to be **protocol-agnostic** (HTTP, custom TCP, etc.) and forwards **raw bytes**.

The corresponding backend service is a simple Spring Boot app named **hello-lb**, running multiple instances (8085, 8086, etc.) to demonstrate load-balancing.

---

# 🧱 **Architecture**

```
                      +-------------------------+
Client (curl/Postman) |   TCP Connection (9000) | 
--------------------->|  Load Balancer Service  |
                      +-----------+-------------+
                                  |
        Round Robin / Weighted / Least Conn / Consistent Hashing
                                  |
                +-----------------+-----------------+
                |                                   |
       +--------v---------+                +--------v---------+
       |  hello-lb :8085  |                |  hello-lb :8086  |
       |  Spring Boot App |                |  Spring Boot App |
       +------------------+                +------------------+
```

---

# 🔧 **Components**

## 1️⃣ **Load Balancer Service (This Project)**

Runs on TCP port **9000** and forwards traffic to backend servers.
Implements:

* **Round Robin**
* **Weighted Round Robin**
* **Least Connections**
* **Consistent Hashing**
* **Connection Mapping**

This service reads configuration from `application.properties`.

---

## 2️⃣ **Backend Service: hello-lb**

This is the actual service that responds to `/hello`.

You run **multiple instances**:

```
java -jar hello-lb.jar --server.port=8085
java -jar hello-lb.jar --server.port=8086
```

Each instance responds with:

```
Response from server on port: 8085
(or)
Response from server on port: 8086
```

### ❓ Why do we need hello-lb?

Because a load balancer alone does nothing unless we have multiple backend instances to distribute requests to.

hello-lb demonstrates:

* How requests are routed across multiple backend instances
* That LB strategies are working
* Real-world scenario of horizontally scalable microservices

---

# ⚙️ **Configuration (application.properties)**

```properties
spring.application.name=load_balancer

lb.backendServers[0]=localhost:8085
lb.backendServers[1]=localhost:8086

lb.connectionMapping.tcp1=tcp2
lb.connectionMapping.tcpX=tcpY

lb.strategy=ROUND_ROBIN
```

Supported strategies:

```
ROUND_ROBIN
WEIGHTED_ROUND_ROBIN
LEAST_CONNECTIONS
CONSISTENT_HASHING
```

Switch anytime by editing this file.

---

# 🧪 **How to Run**

## 1️⃣ Start backend servers (hello-lb)

Open two terminals:

```
java -jar hello-lb.jar --server.port=8085
java -jar hello-lb.jar --server.port=8086
```

## 2️⃣ Start the load balancer

```
mvn spring-boot:run
```

LB listens on:

```
localhost:9000
```

---

# 🧪 **Testing**

### ✔ HTTP Test using curl (per-request LB rotation)

Because browsers & Postman reuse TCP sockets, use curl without keep-alive:

```
curl -v --http1.0 http://localhost:9000/hello
```

Run multiple times:

```
curl -v --http1.0 http://localhost:9000/hello
curl -v --http1.0 http://localhost:9000/hello
curl -v --http1.0 http://localhost:9000/hello
```

Expected (Round Robin):

```
Response from server on port: 8085
Response from server on port: 8086
Response from server on port: 8085
```

---

### ✔ Test using nc (raw TCP)

```
echo -e "GET /hello HTTP/1.1\r\nHost: localhost\r\n\r\n" | nc localhost 9000
```

Repeat multiple times to see round robin.

---

### ✔ Postman

Works, but Postman **reuses TCP connections**, so LB will always choose the same backend.

---

# 🔍 **Load Balancing Strategies Explained**

## ➤ 1. Round Robin

Cycles through backend servers in order:

```
8085 → 8086 → 8085 → 8086 …
```

## ➤ 2. Weighted Round Robin

Servers can have weights such that:

```
8085 (weight 5)
8086 (weight 1)
```

Routing becomes:

```
8085,8085,8085,8085,8085,8086 (repeat)
```

## ➤ 3. Least Connections

Chooses the backend with the fewest **active TCP connections**.

Useful for long-lived connections (WebSockets, streaming).

## ➤ 4. Consistent Hashing

Same client always goes to the same server based on:

```
hash(client_ip) → next node clockwise
```

Minimizes redistribution during scaling.

---

# 🔥 **Important Behavior: Connection-Based Load Balancing**

This LB forwards **raw TCP streams**, not individual HTTP requests.

Meaning:

```
1 TCP connection → 1 selected backend
```

Browsers/Postman use **HTTP Keep-Alive**, so they reuse the same connection → the same backend.

To test per-request LB, use:

```
curl --http1.0
```

---

# 🧱 **Project Structure**

```
load-balancer/
 ├── src/main/java/com/example/load_balancer/
 │    ├── config/LoadBalancerConfig.java
 │    ├── server/TcpLoadBalancerServer.java
 │    ├── strategies/
 │    │      ├── RoundRobinStrategy.java
 │    │      ├── WeightedRoundRobinStrategy.java
 │    │      ├── LeastConnectionsStrategy.java
 │    │      ├── ConsistentHashingStrategy.java
 │    └── LoadBalancerApplication.java
 ├── src/main/resources/application.properties
 └── README.md
```

---

# ⚠️ Limitations

* Not an HTTP-aware proxy (no header parsing, no keep-alive management).
* Not suitable for production load levels.
* Each connection spawns new threads (no async NIO).
* No TLS termination.

---

# 🚀 Future Enhancements

* Support HTTP-aware routing
* Add connection timeout & health checks
* Implement NIO (Netty) for high performance
* Add metrics + dashboard
* Implement request logging

---

# 🎉 Done!

If you want, I can also:

✅ Generate a Postman Collection
✅ Create Docker Compose for LB + hello-lb
✅ Add architecture diagrams (PNG)
✅ Add troubleshooting section

Would you like any of these?
