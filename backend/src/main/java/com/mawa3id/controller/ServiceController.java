package com.mawa3id.controller;

import com.mawa3id.dto.ServiceDTO;
import com.mawa3id.model.Service;
import com.mawa3id.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<ServiceDTO>> getServices(Authentication authentication) {
        UUID userId = (UUID) authentication.getDetails();
        List<ServiceDTO> services = serviceService.getServicesByUserId(userId);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getService(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getDetails();
        ServiceDTO service = serviceService.getServiceById(id);
        return ResponseEntity.ok(service);
    }

    @PostMapping
    public ResponseEntity<ServiceDTO> createService(
            Authentication authentication,
            @Valid @RequestBody ServiceDTO request) {
        UUID userId = (UUID) authentication.getDetails();
        Service service = serviceService.createService(userId, request);
        ServiceDTO response = new ServiceDTO(
                service.getId(),
                service.getName(),
                service.getDurationMinutes(),
                service.getPrice(),
                service.getCreatedAt(),
                service.getUpdatedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDTO> updateService(
            @PathVariable UUID id,
            Authentication authentication,
            @Valid @RequestBody ServiceDTO request) {
        UUID userId = (UUID) authentication.getDetails();
        Service service = serviceService.updateService(id, userId, request);
        ServiceDTO response = new ServiceDTO(
                service.getId(),
                service.getName(),
                service.getDurationMinutes(),
                service.getPrice(),
                service.getCreatedAt(),
                service.getUpdatedAt()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getDetails();
        serviceService.deleteService(id, userId);
        return ResponseEntity.noContent().build();
    }
}
