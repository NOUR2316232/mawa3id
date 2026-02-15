package com.mawa3id.service;

import com.mawa3id.dto.AnalyticsDTO;
import com.mawa3id.model.Appointment;
import com.mawa3id.repository.AppointmentRepository;
import com.mawa3id.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    public AnalyticsDTO getAnalytics(UUID userId) {
        List<Appointment> appointments = appointmentRepository.findByUserId(userId);

        long total = appointments.size();
        long confirmed = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.CONFIRMED)
                .count();
        long cancelled = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.CANCELLED)
                .count();
        long noShow = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.NO_SHOW)
                .count();
        long pending = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.PENDING)
                .count();

        double noShowRate = total > 0 ? (double) noShow / total * 100 : 0;
        double confirmationRate = total > 0 ? (double) confirmed / total * 100 : 0;
        double cancellationRate = total > 0 ? (double) cancelled / total * 100 : 0;

        // Calculate revenue lost (appointments marked as NO_SHOW)
        BigDecimal revenueLost = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.NO_SHOW)
                .map(a -> serviceRepository.findById(a.getServiceId()))
                .filter(java.util.Optional::isPresent)
                .map(s -> s.get().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Estimate revenue saved (assuming 20% reduction in no-shows as industry standard)
        BigDecimal estimatedNoShowReduction = new BigDecimal("0.20");
        BigDecimal revenueSaved = appointments.stream()
                .map(a -> serviceRepository.findById(a.getServiceId()))
                .filter(java.util.Optional::isPresent)
                .map(s -> s.get().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(estimatedNoShowReduction)
                .multiply(new BigDecimal(String.valueOf(noShowRate / 100)));

        BigDecimal totalRevenue = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.CONFIRMED)
                .map(a -> serviceRepository.findById(a.getServiceId()))
                .filter(java.util.Optional::isPresent)
                .map(s -> s.get().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AnalyticsDTO.builder()
                .totalAppointments(total)
                .confirmedAppointments(confirmed)
                .cancelledAppointments(cancelled)
                .noShowAppointments(noShow)
                .pendingAppointments(pending)
                .noShowRate(noShowRate)
                .confirmationRate(confirmationRate)
                .cancellationRate(cancellationRate)
                .totalRevenue(totalRevenue)
                .revenueLost(revenueLost)
                .revenueSaved(revenueSaved)
                .build();
    }
}
