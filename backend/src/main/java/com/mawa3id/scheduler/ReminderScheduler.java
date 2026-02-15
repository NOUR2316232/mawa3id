package com.mawa3id.scheduler;

import com.mawa3id.model.Appointment;
import com.mawa3id.service.AppointmentService;
import com.mawa3id.service.ReminderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ReminderScheduler.class);

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ReminderService reminderService;

    // Run every 10 minutes
    @Scheduled(fixedRate = 600000)
    public void processReminders() {
        logger.info("Starting reminder processing task");

        try {
            // Send first reminder for appointments 24h away
            List<Appointment> appointmentsForTomorrow = appointmentService.getPendingAppointmentsForTomorrow();
            for (Appointment appointment : appointmentsForTomorrow) {
                reminderService.sendFirstReminder(appointment);
            }
            logger.info("Processed {} first reminders", appointmentsForTomorrow.size());

            // Send follow-up reminder for appointments within 6h
            List<Appointment> appointmentsWithin6Hours = appointmentService.getPendingAppointmentsWithin6Hours();
            for (Appointment appointment : appointmentsWithin6Hours) {
                reminderService.sendFollowUpReminder(appointment);
            }
            logger.info("Processed {} follow-up reminders", appointmentsWithin6Hours.size());

            // Mark expired pending appointments as NO_SHOW
            List<Appointment> expiredAppointments = appointmentService.getExpiredPendingAppointments();
            for (Appointment appointment : expiredAppointments) {
                reminderService.markNoShow(appointment);
            }
            logger.info("Marked {} appointments as NO_SHOW", expiredAppointments.size());

        } catch (Exception e) {
            logger.error("Error in reminder processing", e);
        }
    }
}
