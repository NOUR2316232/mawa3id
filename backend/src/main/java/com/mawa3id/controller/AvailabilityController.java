package com.mawa3id.controller;

import com.mawa3id.dto.AvailabilityDTO;
import com.mawa3id.model.Availability;
import com.mawa3id.service.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/availability")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    @GetMapping
    public ResponseEntity<List<AvailabilityDTO>> getAvailabilities(Authentication authentication) {
        UUID userId = (UUID) authentication.getDetails();
        List<AvailabilityDTO> availabilities = availabilityService.getAvailabilitiesByUserId(userId);
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/{dayOfWeek}")
    public ResponseEntity<List<AvailabilityDTO>> getAvailabilitiesByDay(
            @PathVariable Integer dayOfWeek,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getDetails();
        List<AvailabilityDTO> availabilities = availabilityService.getAvailabilitiesByDayOfWeek(userId, dayOfWeek);
        return ResponseEntity.ok(availabilities);
    }

    @PostMapping
    public ResponseEntity<AvailabilityDTO> createAvailability(
            Authentication authentication,
            @Valid @RequestBody AvailabilityDTO request) {
        UUID userId = (UUID) authentication.getDetails();
        Availability availability = availabilityService.createAvailability(userId, request);
        AvailabilityDTO response = new AvailabilityDTO(
                availability.getId(),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityDTO> updateAvailability(
            @PathVariable UUID id,
            Authentication authentication,
            @Valid @RequestBody AvailabilityDTO request) {
        UUID userId = (UUID) authentication.getDetails();
        Availability availability = availabilityService.updateAvailability(id, userId, request);
        AvailabilityDTO response = new AvailabilityDTO(
                availability.getId(),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getDetails();
        availabilityService.deleteAvailability(id, userId);
        return ResponseEntity.noContent().build();
    }
}
