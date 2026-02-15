package com.mawa3id.repository;

import com.mawa3id.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByUserId(UUID userId);

    Optional<Appointment> findByConfirmationToken(String token);

    List<Appointment> findByUserIdAndAppointmentDate(UUID userId, LocalDate appointmentDate);

    @Query("SELECT a FROM Appointment a WHERE a.userId = :userId " +
           "AND a.appointmentDate >= :startDate " +
           "AND a.appointmentDate <= :endDate")
    List<Appointment> findByUserIdAndDateRange(@Param("userId") UUID userId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Appointment a WHERE a.userId = :userId AND a.status = :status")
    List<Appointment> findByUserIdAndStatus(@Param("userId") UUID userId,
                                            @Param("status") Appointment.AppointmentStatus status);

    List<Appointment> findByStatusAndAppointmentDate(Appointment.AppointmentStatus status, LocalDate appointmentDate);

    List<Appointment> findByStatusAndAppointmentDateAndStartTimeBetween(
            Appointment.AppointmentStatus status,
            LocalDate appointmentDate,
            LocalTime start,
            LocalTime end
    );

    List<Appointment> findByStatusAndAppointmentDateAndStartTimeBefore(
            Appointment.AppointmentStatus status,
            LocalDate appointmentDate,
            LocalTime time
    );

    List<Appointment> findByStatusAndAppointmentDateAndStartTimeAfter(
            Appointment.AppointmentStatus status,
            LocalDate appointmentDate,
            LocalTime time
    );

    List<Appointment> findByStatusAndAppointmentDateBefore(Appointment.AppointmentStatus status, LocalDate date);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.userId = :userId AND a.status = 'NO_SHOW'")
    long countNoShowsByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.userId = :userId")
    long countTotalByUserId(@Param("userId") UUID userId);
}