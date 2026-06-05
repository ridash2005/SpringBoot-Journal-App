package io.rickarya.journalApp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/journal/health")
@Slf4j
public class HealthCheckController {

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("application", "Journal App");
        status.put("version", "1.0.0");
        status.put("timestamp", System.currentTimeMillis());
        status.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime());
        return ResponseEntity.ok(status);
    }

    @GetMapping("/ready")
    public ResponseEntity<Map<String, String>> readiness() {
        return ResponseEntity.ok(Map.of("ready", "true"));
    }

}
