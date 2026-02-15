package com.mawa3id.service;

import com.mawa3id.dto.AppointmentDTO;
import com.mawa3id.model.Appointment;
import com.mawa3id.model.Availability;
import com.mawa3id.model.Service;
import com.mawa3id.repository.AppointmentRepository;
import com.mawa3id.repository.AvailabilityRepository;
import com.mawa3id.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    public Appointment createAppointment(UUID userId, AppointmentDTO request) {
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (!service.getUserId().equals(userId)) {
            throw new RuntimeException("Service does not belong to this user");
        }

        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setServiceId(request.getServiceId());
        appointment.setCustomerName(request.getCustomerName());
        appointment.setCustomerPhone(request.getCustomerPhone());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(resolveEndTime(request, service));
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        appointment.setConfirmationToken(UUID.randomUUID().toString());

        return appointmentRepository.save(appointment);
    }

    public Appointment createPublicAppointment(UUID businessId, AppointmentDTO request) {
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (!service.getUserId().equals(businessId)) {
            throw new RuntimeException("Service does not belong to this business");
        }

        LocalTime endTime = resolveEndTime(request, service);
        if (!endTime.isAfter(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        validateWithinAvailability(businessId, request.getAppointmentDate(), request.getStartTime(), endTime);
        validateNoOverlap(businessId, request.getAppointmentDate(), request.getStartTime(), endTime);

        Appointment appointment = new Appointment();
        appointment.setUserId(businessId);
        appointment.setServiceId(request.getServiceId());
        appointment.setCustomerName(request.getCustomerName());
        appointment.setCustomerPhone(request.getCustomerPhone());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(endTime);
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        appointment.setConfirmationToken(UUID.randomUUID().toString());

        return appointmentRepository.save(appointment);
    }

    public List<AppointmentDTO> getAppointmentsByUserId(UUID userId) {
        return appointmentRepository.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO getAppointmentById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public List<AppointmentDTO> getAppointmentsByDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByUserIdAndDateRange(userId, startDate, endDate)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Appointment updateAppointmentStatus(UUID appointmentId, UUID userId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this appointment");
        }

        try {
            appointment.setStatus(Appointment.AppointmentStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }

        return appointmentRepository.save(appointment);
    }

    public Appointment confirmAppointment(String confirmationToken) {
        Appointment appointment = appointmentRepository.findByConfirmationToken(confirmationToken)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        return appointmentRepository.save(appointment);
    }

    public Appointment cancelAppointment(String confirmationToken) {
        Appointment appointment = appointmentRepository.findByConfirmationToken(confirmationToken)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(UUID appointmentId, UUID userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this appointment");
        }

        appointmentRepository.delete(appointment);
    }

    public List<Appointment> getPendingAppointmentsForTomorrow() {
        return appointmentRepository.findByStatusAndAppointmentDate(
                Appointment.AppointmentStatus.PENDING,
                LocalDate.now().plusDays(1)
        );
    }

    public List<Appointment> getPendingAppointmentsWithin6Hours() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixHoursFromNow = now.plusHours(6);

        LocalDate today = now.toLocalDate();
        LocalDate endDate = sixHoursFromNow.toLocalDate();
        LocalTime nowTime = now.toLocalTime();
        LocalTime endTime = sixHoursFromNow.toLocalTime();

        if (today.equals(endDate)) {
            return appointmentRepository.findByStatusAndAppointmentDateAndStartTimeBetween(
                    Appointment.AppointmentStatus.PENDING,
                    today,
                    nowTime,
                    endTime
            );
        }

        List<Appointment> result = new ArrayList<>();
        result.addAll(appointmentRepository.findByStatusAndAppointmentDateAndStartTimeAfter(
                Appointment.AppointmentStatus.PENDING,
                today,
                nowTime
        ));
        result.addAll(appointmentRepository.findByStatusAndAppointmentDateAndStartTimeBefore(
                Appointment.AppointmentStatus.PENDING,
                endDate,
                endTime
        ));
        return result;
    }

    public List<Appointment> getExpiredPendingAppointments() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Appointment> result = new ArrayList<>();
        result.addAll(appointmentRepository.findByStatusAndAppointmentDateBefore(
                Appointment.AppointmentStatus.PENDING,
                today
        ));
        result.addAll(appointmentRepository.findByStatusAndAppointmentDateAndStartTimeBefore(
                Appointment.AppointmentStatus.PENDING,
                today,
                now
        ));
        return result;
    }

    private LocalTime resolveEndTime(AppointmentDTO request, Service service) {
        if (request.getEndTime() != null) {
            return request.getEndTime();
        }
        return request.getStartTime().plusMinutes(service.getDurationMinutes());
    }

    private void validateWithinAvailability(UUID businessId, LocalDate appointmentDate, LocalTime startTime, LocalTime endTime) {
        int dayOfWeek = appointmentDate.getDayOfWeek().getValue() % 7;

        List<Availability> availabilities = availabilityRepository.findByUserIdAndDayOfWeek(businessId, dayOfWeek);
        boolean insideAnyWindow = availabilities.stream().anyMatch(availability ->
                !startTime.isBefore(availability.getStartTime()) && !endTime.isAfter(availability.getEndTime())
        );

        if (!insideAnyWindow) {
            throw new RuntimeException("Requested slot is outside business availability");
        }
    }

    private void validateNoOverlap(UUID businessId, LocalDate appointmentDate, LocalTime startTime, LocalTime endTime) {
        List<Appointment> existingAppointments = appointmentRepository.findByUserIdAndAppointmentDate(businessId, appointmentDate);
        boolean overlaps = existingAppointments.stream()
                .filter(a -> a.getStatus() != Appointment.AppointmentStatus.CANCELLED)
                .anyMatch(a -> startTime.isBefore(a.getEndTime()) && endTime.isAfter(a.getStartTime()));

        if (overlaps) {
            throw new RuntimeException("Requested slot is already booked");
        }
    }

    private AppointmentDTO toDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setServiceId(appointment.getServiceId());
        dto.setCustomerName(appointment.getCustomerName());
        dto.setCustomerPhone(appointment.getCustomerPhone());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStartTime(appointment.getStartTime());
        dto.setEndTime(appointment.getEndTime());
        dto.setStatus(appointment.getStatus().name());
        dto.setConfirmationToken(appointment.getConfirmationToken());
        dto.setCreatedAt(appointment.getCreatedAt());
        dto.setUpdatedAt(appointment.getUpdatedAt());
        return dto;
    }
}