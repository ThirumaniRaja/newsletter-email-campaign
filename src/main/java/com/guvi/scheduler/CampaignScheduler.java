package com.guvi.scheduler;

import com.guvi.service.CampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class CampaignScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CampaignScheduler.class);

    @Autowired
    private CampaignService campaignService;

    // Check for pending campaigns every 1 minute (60000 milliseconds)
    @Scheduled(fixedDelay = 60000)
    public void processPendingCampaigns() {
        logger.info("Checking for pending campaigns to send...");
        try {
            campaignService.processPendingCampaigns();
        } catch (Exception e) {
            logger.error("Error processing pending campaigns", e);
        }
    }
}

