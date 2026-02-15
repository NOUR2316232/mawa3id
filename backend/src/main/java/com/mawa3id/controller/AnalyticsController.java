package com.mawa3id.controller;

import com.mawa3id.dto.AnalyticsDTO;
import com.mawa3id.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<AnalyticsDTO> getAnalytics(Authentication authentication) {
        UUID userId = (UUID) authentication.getDetails();
        AnalyticsDTO analytics = analyticsService.getAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }
}
