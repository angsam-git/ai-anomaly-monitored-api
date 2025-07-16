package com.angsam.aimonitoredapi.dto;

public class LogEntry {
    private long timestamp;
    private String endpoint;
    private int status;
    private long responseTimeMs;
    private String requestId;

    public LogEntry() {}

    public LogEntry(long timestamp, String endpoint, int status, long responseTimeMs, String requestId) {
        this.timestamp = timestamp;
        this.endpoint = endpoint;
        this.status = status;
        this.responseTimeMs = responseTimeMs;
        this.requestId = requestId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
