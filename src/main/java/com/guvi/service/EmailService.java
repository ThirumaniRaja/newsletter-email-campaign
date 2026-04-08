package com.guvi.service;

import com.guvi.entity.Campaign;
import com.guvi.entity.EmailLog;
import com.guvi.entity.EmailLog.EmailStatus;
import com.guvi.repository.EmailLogRepository;
import com.guvi.repository.SubscriberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private EmailLogRepository emailLogRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    public void sendCampaign(Campaign campaign) {
        logger.info("Starting email send for campaign: {} to mailing list: {}", 
            campaign.getId(), campaign.getMailingList().getId());

        campaign.getMailingList().getSubscribers().forEach(subscriber -> {
            try {
                sendEmail(campaign, subscriber);
                logEmail(campaign, subscriber.getEmail(), subscriber.getName(), EmailStatus.SENT, null);
                logger.info("Email sent successfully to: {} for campaign: {}", 
                    subscriber.getEmail(), campaign.getId());
            } catch (Exception e) {
                logEmail(campaign, subscriber.getEmail(), subscriber.getName(), EmailStatus.FAILED, e.getMessage());
                logger.error("Failed to send email to: {} for campaign: {}", 
                    subscriber.getEmail(), campaign.getId(), e);
            }
        });

        logger.info("Email send completed for campaign: {}", campaign.getId());
    }

    private void sendEmail(Campaign campaign, com.guvi.entity.Subscriber subscriber) {
        // Simulated email sending - in production, use actual email service (JavaMail, SendGrid, etc.)
        logger.info("=== EMAIL SEND SIMULATION ===");
        logger.info("To: {}", subscriber.getEmail());
        logger.info("Subject: {}", campaign.getSubject());
        logger.info("Content: {}", campaign.getContent());
        logger.info("Recipient Name: {}", subscriber.getName());
        logger.info("Campaign: {} (ID: {})", campaign.getName(), campaign.getId());
        logger.info("Sent at: {}", LocalDateTime.now());
        logger.info("============================");

        // Simulate successful sending
        // In production, check response from email service
    }

    private void logEmail(Campaign campaign, String email, String name, EmailStatus status, String errorMessage) {
        EmailLog emailLog = EmailLog.builder()
                .campaign(campaign)
                .subscriberEmail(email)
                .subscriberName(name)
                .status(status)
                .errorMessage(errorMessage)
                .build();

        emailLogRepository.save(emailLog);
    }

    public List<EmailLog> getEmailLogs(Campaign campaign) {
        return emailLogRepository.findByCampaign(campaign);
    }
}

