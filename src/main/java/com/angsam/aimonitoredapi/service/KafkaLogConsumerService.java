package com.angsam.aimonitoredapi.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.angsam.aimonitoredapi.dto.AnomalyResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaLogConsumerService {

    @Value("${kafka-bootstrap-server}")
    private String kafkaBootstrapServer;

    @Value("${kafka-sink}")
    private String kafkaSinkTopic;

    private final ObjectMapper objectMapper;

    public KafkaLogConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<AnomalyResult> getLogs() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "log-reader-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        List<AnomalyResult> anomalyResults = new ArrayList<>();

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            List<TopicPartition> partitions = consumer.partitionsFor(kafkaSinkTopic)
                    .stream()
                    .map(partitionInfo -> new TopicPartition(kafkaSinkTopic, partitionInfo.partition()))
                    .toList();

            consumer.assign(partitions);

            consumer.seekToBeginning(Collections.emptyList());

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

            for (ConsumerRecord<String, String> record : records) {
                try {
                    AnomalyResult anomalyResult = objectMapper.readValue(record.value(), AnomalyResult.class);
                    anomalyResults.add(anomalyResult);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        return anomalyResults;
    }

}
