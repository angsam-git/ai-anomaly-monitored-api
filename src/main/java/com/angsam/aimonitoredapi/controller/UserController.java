package com.angsam.aimonitoredapi.controller;

import com.angsam.aimonitoredapi.dto.LogEntry;
import com.angsam.aimonitoredapi.dto.OrderResponse;
import com.angsam.aimonitoredapi.dto.UserResponse;
import com.angsam.aimonitoredapi.service.KafkaLogProducerService;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private KafkaLogProducerService kafkaLogProducerService;

    // Simple rate limit for /users and /orders combined
    private final RateLimiter limiter = RateLimiter.create(10.0); // 10 reqs/sec

    private boolean rateLimitExceeded() {
        return !limiter.tryAcquire();
    }

    private void simulateLatency() throws InterruptedException {
        Thread.sleep(2000); // Simulate high latency
    }

    @GetMapping("/users")
    public ResponseEntity<UserResponse> getUsers(
            @RequestParam(name = "anomaly", required = false) Boolean anomaly,
            HttpServletRequest request
    ) throws InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String requestId = UUID.randomUUID().toString();

        if (rateLimitExceeded()) {
            int status = 429;
            log("/users", "GET", stopwatch, status, request, 0, requestId);
            return ResponseEntity.status(status).body(null);
        }

        if (Boolean.TRUE.equals(anomaly)) {
            simulateLatency();
        }

        UserResponse user = new UserResponse();
        user.setId(UUID.randomUUID().toString());
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        int status = 200;
        int responseSize = user.toString().getBytes().length; // lightweight estimate
        log("/users", "GET", stopwatch, status, request, responseSize, requestId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/orders")
    public ResponseEntity<OrderResponse> getOrders(
            @RequestParam(name = "anomaly", required = false) Boolean anomaly,
            HttpServletRequest request
    ) throws InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String requestId = UUID.randomUUID().toString();

        if (rateLimitExceeded()) {
            int status = 429;
            log("/orders", "GET", stopwatch, status, request, 0, requestId);
            return ResponseEntity.status(status).body(null);
        }

        if (Boolean.TRUE.equals(anomaly)) {
            simulateLatency();
        }

        OrderResponse order = new OrderResponse();
        order.setOrderId(UUID.randomUUID().toString());
        order.setDescription("Sample order");
        order.setAmount(99.99);

        int status = 200;
        int responseSize = order.toString().getBytes().length;
        log("/orders", "GET", stopwatch, status, request, responseSize, requestId);
        return ResponseEntity.ok(order);
    }

    private void log(String endpoint,
                     String method,
                     Stopwatch stopwatch,
                     int status,
                     HttpServletRequest request,
                     int responseSizeBytes,
                     String requestId) {

        long duration = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        // Count headers
        int headerCount = 0;
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            headerNames.nextElement();
            headerCount++;
        }

        // Count query parameters
        int queryCount = request.getParameterMap().size();

        LogEntry logEntry = new LogEntry(
                System.currentTimeMillis(),
                endpoint,
                method,
                status,
                duration,
                queryCount,
                headerCount,
                responseSizeBytes,
                requestId
        );

        kafkaLogProducerService.sendLog(logEntry);
    }

}
