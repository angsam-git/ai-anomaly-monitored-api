package com.angsam.aimonitoredapi.service;

import com.angsam.aimonitoredapi.dto.LogEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaLogProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaLogProducerService.class);

    @Value("${kafka-topic}")
    private String kafkaTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendLog(LogEntry logEntry) {
        try {
            String json = objectMapper.writeValueAsString(logEntry);
            kafkaTemplate.send(kafkaTopic, json);
            logger.info("Sent log to Kafka topic '{}': {}", kafkaTopic, json);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize log entry for Kafka", e);
        } catch (Exception e) {
            logger.error("Failed to send log to Kafka", e);
        }
    }

}