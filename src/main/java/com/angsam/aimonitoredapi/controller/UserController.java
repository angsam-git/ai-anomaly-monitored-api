package com.angsam.aimonitoredapi.controller;

import com.angsam.aimonitoredapi.dto.LogEntry;
import com.angsam.aimonitoredapi.dto.OrderResponse;
import com.angsam.aimonitoredapi.dto.UserResponse;
import com.angsam.aimonitoredapi.service.KafkaLogProducerService;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.RateLimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private KafkaLogProducerService kafkaLogProducerService;

    private final RateLimiter limiter = RateLimiter.create(10.0); // 10 reqs/sec

    private boolean rateLimitExceeded() {
        return !limiter.tryAcquire();
    }

    private void simulateLatency() throws InterruptedException {
        Thread.sleep(2000); // Simulate high latency
    }

    // Optional parameter anomaly in both /users and /orders to allow for latency simulation
    // If anomaly param is provided and true, a delay is simulated in the response
    @GetMapping("/users")
    public ResponseEntity<UserResponse> getUsers(@RequestParam(name = "anomaly", required = false) Boolean anomaly) throws InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String requestId = UUID.randomUUID().toString();
        int status;

        if (rateLimitExceeded()) {
            status = 429;
            log("/users", stopwatch, status, requestId);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }

        if(anomaly != null && anomaly) {
            simulateLatency();
        }

        UserResponse user = new UserResponse();
        user.setId(UUID.randomUUID().toString());
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        status = 200;

        log("/users", stopwatch, status, requestId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/orders")
    public ResponseEntity<OrderResponse> getOrders(@RequestParam(name = "anomaly", required = false) Boolean anomaly) throws InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String requestId = UUID.randomUUID().toString();
        int status;

        if (rateLimitExceeded()) {
            status = 429;
            log("/orders", stopwatch, status, requestId);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }

        if(anomaly != null && anomaly) {
            simulateLatency();
        }

        OrderResponse order = new OrderResponse();
        order.setOrderId(UUID.randomUUID().toString());
        order.setDescription("Sample order");
        order.setAmount(99.99);
        status = 200;

        log("/orders", stopwatch, status, requestId);
        return ResponseEntity.ok(order);
    }

    private void log(String endpoint, Stopwatch stopwatch, int status, String requestId) {
        long duration = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        LogEntry logEntry = new LogEntry(System.currentTimeMillis(), endpoint, status, duration, requestId);
        kafkaLogProducerService.sendLog(logEntry);
    }
}
