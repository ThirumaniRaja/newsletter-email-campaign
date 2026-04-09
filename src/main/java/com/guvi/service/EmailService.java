package com.guvi.service;

import com.guvi.entity.Campaign;
import com.guvi.entity.EmailLog;
import com.guvi.entity.EmailLog.EmailStatus;
import com.guvi.entity.Subscriber;
import com.guvi.repository.EmailLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private EmailLogRepository emailLogRepository;

    /**
     * MOCKING campaign email to every subscriber in the linked mailing list.
     */
    public void sendCampaign(Campaign campaign) {
        List<Subscriber> subscribers = campaign.getMailingList().getSubscribers();
        if (subscribers == null || subscribers.isEmpty()) {
            logger.warn("[MOCK] Campaign '{}' (ID={}) has no subscribers — skipping send.",
                    campaign.getName(), campaign.getId());
            return;
        }

        logger.info("[MOCK] ===== Starting campaign send =====");
        logger.info("[MOCK] Campaign   : {} (ID={})", campaign.getName(), campaign.getId());
        logger.info("[MOCK] Subject    : {}", campaign.getSubject());
        logger.info("[MOCK] Mailing List: {} (ID={})",
                campaign.getMailingList().getName(), campaign.getMailingList().getId());
        logger.info("[MOCK] Subscribers: {}", subscribers.size());
        logger.info("[MOCK] Started at : {}", LocalDateTime.now().format(FORMATTER));
        logger.info("[MOCK] ======================================");

        int sent = 0, failed = 0;

        for (Subscriber subscriber : subscribers) {
            try {
                mockSendEmail(campaign, subscriber);
                logEmail(campaign, subscriber.getEmail(), subscriber.getName(), EmailStatus.SENT, null);
                sent++;
            } catch (Exception e) {
                logger.error("[MOCK] Failed to send to {} — {}", subscriber.getEmail(), e.getMessage());
                logEmail(campaign, subscriber.getEmail(), subscriber.getName(), EmailStatus.FAILED, e.getMessage());
                failed++;
            }
        }

        logger.info("[MOCK] ===== Campaign send complete =====");
        logger.info("[MOCK] Campaign : {} (ID={})", campaign.getName(), campaign.getId());
        logger.info("[MOCK] Sent     : {}", sent);
        logger.info("[MOCK] Failed   : {}", failed);
        logger.info("[MOCK] Total    : {}", subscribers.size());
        logger.info("[MOCK] Finished : {}", LocalDateTime.now().format(FORMATTER));
        logger.info("[MOCK] =====================================");
    }


    private void mockSendEmail(Campaign campaign, Subscriber subscriber) {
        logger.info("[MOCK] ------ Email Sent ------");
        logger.info("[MOCK] To      : {} <{}>", subscriber.getName(), subscriber.getEmail());
        logger.info("[MOCK] Subject : {}", campaign.getSubject());
        logger.info("[MOCK] Body    : {}", campaign.getContent());
        logger.info("[MOCK] Time    : {}", LocalDateTime.now().format(FORMATTER));
        logger.info("[MOCK] ---------------------------");

    }

    private void logEmail(Campaign campaign, String email, String name,
                          EmailStatus status, String errorMessage) {
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
