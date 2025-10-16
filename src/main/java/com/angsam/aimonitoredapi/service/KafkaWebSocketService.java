package com.angsam.aimonitoredapi.service;

import com.angsam.aimonitoredapi.controller.WebSocketController;
import com.angsam.aimonitoredapi.dto.AnomalyResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaWebSocketService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaWebSocketService.class);

    @Autowired
    private WebSocketController webSocketController;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka-sink}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message) {
        try {
            AnomalyResult anomalyResult = objectMapper.readValue(message, AnomalyResult.class);
            String jsonMessage = objectMapper.writeValueAsString(anomalyResult);
            
            for (WebSocketSession session : webSocketController.getSessions().values()) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(jsonMessage));
                }
            }
        } catch (Exception e) {
            logger.error("Error processing Kafka message", e);
        }
    }

}