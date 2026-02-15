package com.mawa3id.controller;

import com.mawa3id.dto.AppointmentDTO;
import com.mawa3id.dto.AvailabilityDTO;
import com.mawa3id.dto.PublicBookingProfileDTO;
import com.mawa3id.dto.ServiceDTO;
import com.mawa3id.model.Appointment;
import com.mawa3id.model.User;
import com.mawa3id.repository.AvailabilityRepository;
import com.mawa3id.repository.ServiceRepository;
import com.mawa3id.repository.UserRepository;
import com.mawa3id.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class PublicBookingController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/booking/{businessId}")
    public ResponseEntity<PublicBookingProfileDTO> getBookingProfile(@PathVariable UUID businessId) {
        User business = userRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        List<ServiceDTO> services = serviceRepository.findByUserId(businessId)
                .stream()
                .map(service -> new ServiceDTO(
                        service.getId(),
                        service.getName(),
                        service.getDurationMinutes(),
                        service.getPrice(),
                        service.getCreatedAt(),
                        service.getUpdatedAt()
                ))
                .collect(Collectors.toList());

        List<AvailabilityDTO> availability = availabilityRepository.findByUserId(businessId)
                .stream()
                .map(a -> new AvailabilityDTO(a.getId(), a.getDayOfWeek(), a.getStartTime(), a.getEndTime()))
                .collect(Collectors.toList());

        PublicBookingProfileDTO response = new PublicBookingProfileDTO(
                business.getId(),
                business.getBusinessName(),
                business.getPhone(),
                services,
                availability
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/booking/{businessId}/appointments")
    public ResponseEntity<AppointmentDTO> createPublicAppointment(
            @PathVariable UUID businessId,
            @Valid @RequestBody AppointmentDTO request) {
        Appointment appointment = appointmentService.createPublicAppointment(businessId, request);

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
}