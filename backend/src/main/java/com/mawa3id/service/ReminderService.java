package com.mawa3id.service;

import com.mawa3id.model.Appointment;
import com.mawa3id.model.ReminderLog;
import com.mawa3id.model.Service;
import com.mawa3id.repository.AppointmentRepository;
import com.mawa3id.repository.ReminderLogRepository;
import com.mawa3id.repository.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class ReminderService {

    private static final Logger logger = LoggerFactory.getLogger(ReminderService.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ReminderLogRepository reminderLogRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private SmsNotificationService smsNotificationService;

    public void sendFirstReminder(Appointment appointment) {
        try {
            Service service = serviceRepository.findById(appointment.getServiceId())
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            String message = String.format(
                    "Hi %s, your appointment for %s is scheduled at %s. Reply YES to confirm or NO to cancel.",
                    appointment.getCustomerName(),
                    service.getName(),
                    appointment.getStartTime()
            );

            smsNotificationService.sendSMS(appointment.getCustomerPhone(), message);

            ReminderLog log = new ReminderLog();
            log.setAppointmentId(appointment.getId());
            log.setType(ReminderLog.ReminderType.FIRST_REMINDER);
            log.setStatus(ReminderLog.ReminderStatus.SENT);
            reminderLogRepository.save(log);

            logger.info("First reminder sent for appointment: " + appointment.getId());
        } catch (Exception e) {
            logger.error("Failed to send first reminder for appointment: " + appointment.getId(), e);
            ReminderLog log = new ReminderLog();
            log.setAppointmentId(appointment.getId());
            log.setType(ReminderLog.ReminderType.FIRST_REMINDER);
            log.setStatus(ReminderLog.ReminderStatus.FAILED);
            reminderLogRepository.save(log);
        }
    }

    public void sendFollowUpReminder(Appointment appointment) {
        try {
            Service service = serviceRepository.findById(appointment.getServiceId())
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            String message = String.format(
                    "Reminder: Your appointment for %s is in 6 hours at %s. Please confirm.",
                    service.getName(),
                    appointment.getStartTime()
            );

            smsNotificationService.sendSMS(appointment.getCustomerPhone(), message);

            ReminderLog log = new ReminderLog();
            log.setAppointmentId(appointment.getId());
            log.setType(ReminderLog.ReminderType.FOLLOW_UP);
            log.setStatus(ReminderLog.ReminderStatus.SENT);
            reminderLogRepository.save(log);

            logger.info("Follow-up reminder sent for appointment: " + appointment.getId());
        } catch (Exception e) {
            logger.error("Failed to send follow-up reminder for appointment: " + appointment.getId(), e);
            ReminderLog log = new ReminderLog();
            log.setAppointmentId(appointment.getId());
            log.setType(ReminderLog.ReminderType.FOLLOW_UP);
            log.setStatus(ReminderLog.ReminderStatus.FAILED);
            reminderLogRepository.save(log);
        }
    }

    public void markNoShow(Appointment appointment) {
        appointment.setStatus(Appointment.AppointmentStatus.NO_SHOW);
        appointmentRepository.save(appointment);
        logger.info("Appointment marked as NO_SHOW: " + appointment.getId());
    }
}


