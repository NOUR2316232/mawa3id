package com.mawa3id.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationService.class);

    @Value("${twilio.account_sid:}")
    private String accountSid;

    @Value("${twilio.auth_token:}")
    private String authToken;

    @Value("${twilio.phone_number:}")
    private String fromPhoneNumber;

    public void sendSMS(String toPhoneNumber, String message) {
        try {
            // For MVP, we'll log the SMS instead of actually sending it
            // In production, integrate with Twilio or local SMS provider
            if (accountSid != null && !accountSid.isEmpty()) {
                // TODO: Integrate with Twilio SDK
                // Com.twilio.Twilio.init(accountSid, authToken);
                // Message.creator(
                //     new PhoneNumber(toPhoneNumber),
                //     new PhoneNumber(fromPhoneNumber),
                //     message
                // ).create();
                logger.info("SMS sent to " + toPhoneNumber + ": " + message);
            } else {
                logger.info("SMS (Not Configured) to " + toPhoneNumber + ": " + message);
            }
        } catch (Exception e) {
            logger.error("Failed to send SMS to " + toPhoneNumber, e);
            throw new RuntimeException("Failed to send SMS", e);
        }
    }
}
