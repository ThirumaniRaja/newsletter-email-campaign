package com.guvi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NewsletterEmailCampaign {
    public static void main(String[] args) {
        SpringApplication.run(NewsletterEmailCampaign.class, args);
    }
}


