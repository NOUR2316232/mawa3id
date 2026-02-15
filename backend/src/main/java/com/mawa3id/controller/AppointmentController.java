package com.mawa3id.controller;

import com.mawa3id.dto.AppointmentDTO;
import com.mawa3id.model.Appointment;
import com.mawa3id.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAppointments(Authentication authentication) {
        UUID userId = (UUID) authentication.getDetails();
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByUserId(userId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointment(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getDetails();
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getDetails();
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(appointments);
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(
            Authentication authentication,
            @Valid @RequestBody AppointmentDTO request) {
        UUID userId = (UUID) authentication.getDetails();
        Appointment appointment = appointmentService.createAppointment(userId, request);

        AppointmentDTO response = new AppointmentDTO();
        response.setId(appointment.getId());
        response.setServiceId(appointment.getServiceId());
        response.setCustomerName(appointment.getCustomerName());
        response.setCustomerPhone(appointment.getCustomerPhone());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setStartTime(appointment.getStartTime());
        response.setEndTime(appointment.getEndTime());
        response.setStatus(appointment.getStatus().name());
        response.setConfirmationToken(appointment.getConfirmationToken());
        response.setCreatedAt(appointment.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentDTO> updateAppointmentStatus(
            @PathVariable UUID id,
            @RequestParam String status,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getDetails();
        Appointment appointment = appointmentService.updateAppointmentStatus(id, userId, status);

        AppointmentDTO response = new AppointmentDTO();
        response.setId(appointment.getId());
        response.setServiceId(appointment.getServiceId());
        response.setCustomerName(appointment.getCustomerName());
        response.setCustomerPhone(appointment.getCustomerPhone());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setStartTime(appointment.getStartTime());
        response.setEndTime(appointment.getEndTime());
        response.setStatus(appointment.getStatus().name());
        response.setCreatedAt(appointment.getCreatedAt());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getDetails();
        appointmentService.deleteAppointment(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/public/confirm/{token}")
    public ResponseEntity<String> confirmAppointment(@PathVariable String token) {
        appointmentService.confirmAppointment(token);
        return ResponseEntity.ok("Appointment confirmed successfully");
    }

    @PostMapping("/public/cancel/{token}")
    public ResponseEntity<String> cancelAppointment(@PathVariable String token) {
        appointmentService.cancelAppointment(token);
        return ResponseEntity.ok("Appointment cancelled successfully");
    }
}
