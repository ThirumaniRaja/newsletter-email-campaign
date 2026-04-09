package com.guvi.scheduler;

import com.guvi.service.CampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@EnableScheduling
public class CampaignScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CampaignScheduler.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private CampaignService campaignService;

    /**
     * Polls every N ms (configured via app.scheduler.fixedDelay) for SCHEDULED campaigns
     * whose scheduledFor time has passed.
     * Triggers mock email send + status update to SENT (or FAILED on error).
     *
     * To speed up testing, set app.scheduler.fixedDelay=10000 in application.properties.
     */
    @Scheduled(fixedDelayString = "${app.scheduler.fixedDelay:60000}")
    public void processPendingCampaigns() {
        logger.info("[SCHEDULER] Tick at {} — checking for due campaigns...",
                LocalDateTime.now().format(FORMATTER));
        try {
            campaignService.processPendingCampaigns();
        } catch (Exception e) {
            logger.error("[SCHEDULER] Unexpected error while processing campaigns: {}", e.getMessage(), e);
        }
    }
}
