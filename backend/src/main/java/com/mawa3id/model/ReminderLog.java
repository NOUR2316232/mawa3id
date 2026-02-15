package com.mawa3id.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reminder_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderLog {

    @Id
    @UuidGenerator
    @Column
    private UUID id;

    @Column(nullable = false)
    private UUID appointmentId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderStatus status;

    public enum ReminderType {
        FIRST_REMINDER, FOLLOW_UP
    }

    public enum ReminderStatus {
        SENT, FAILED
    }
}
