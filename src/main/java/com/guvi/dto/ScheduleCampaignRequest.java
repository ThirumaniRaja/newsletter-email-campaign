package com.guvi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleCampaignRequest {
    private Long campaignId;
    private java.time.LocalDateTime scheduledFor;
}

