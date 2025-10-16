package com.angsam.aimonitoredapi.dto;

public class AnomalyResult {

    private LogEntry log;
    private double error;
    private boolean anomaly;

    public AnomalyResult() {}

    public AnomalyResult(LogEntry log, double error, boolean anomaly) {
        this.log = log;
        this.error = error;
        this.anomaly = anomaly;
    }

    public LogEntry getLog() {
        return log;
    }

    public void setLog(LogEntry log) {
        this.log = log;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public boolean isAnomaly() {
        return anomaly;
    }

    public void setAnomaly(boolean anomaly) {
        this.anomaly = anomaly;
    }
    
}