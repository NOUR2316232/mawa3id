package com.mawa3id.repository;

import com.mawa3id.model.ReminderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderLogRepository extends JpaRepository<ReminderLog, UUID> {
    List<ReminderLog> findByAppointmentId(UUID appointmentId);
    
    @Query("SELECT r FROM ReminderLog r WHERE r.appointmentId = :appointmentId AND r.type = :type")
    List<ReminderLog> findByAppointmentIdAndType(@Param("appointmentId") UUID appointmentId,
                                                  @Param("type") ReminderLog.ReminderType type);
}
