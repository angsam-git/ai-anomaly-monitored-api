package com.angsam.aimonitoredapi.controller;

import com.angsam.aimonitoredapi.dto.AnomalyResult;
import com.angsam.aimonitoredapi.service.KafkaLogConsumerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LogController {

    @Autowired
    private KafkaLogConsumerService kafkaLogConsumerService;

    @GetMapping("/logs")
    public ResponseEntity<List<AnomalyResult>> getLogs() {

        List<AnomalyResult> anomalyResults = kafkaLogConsumerService.getLogs();
        return ResponseEntity.ok(anomalyResults);
    }

}