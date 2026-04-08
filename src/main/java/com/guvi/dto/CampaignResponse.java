package com.guvi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignResponse {
    private Long id;
    private String name;
    private String subject;
    private String content;
    private String status;
    private Long mailingListId;
    private String mailingListName;
    private LocalDateTime scheduledFor;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer totalSubscribers;
}

