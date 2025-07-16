# AI Anomaly Detection - Monitored API

This is the **Spring Boot API application** being monitored by an AI-powered anomaly detection system. It emits structured logs to Kafka, where a real-time analytics pipeline (Flink, PyTorch) inspects and flags abnormal patterns in API usage. It also contains the API which consumes the enriched data from a sink topic in Kafka in order to serve a real-time React dashboard.

---

## Dependencies

Kafka, flink, and the pytorch model must be running to consume logs from this app.

https://github.com/angsam-git/flink-anomaly-detection

## Features

- Exposes REST endpoints (e.g. `/users`, `/orders`)
- Publishes API access logs to a Kafka topic
- Integrates with an anomaly detection pipeline
- Container-ready for Docker-based deployments

## Build & Run

### Build the application

```bash
mvn clean install
```

### Build the docker image
```bash
docker build -t ai-anomaly-monitored-api .
```

### Run the container
```bash
docker run -d --name ai-anomaly-monitored-api --network shared-kafka-net -p 8080:8080 ai-anomaly-monitored-api
```