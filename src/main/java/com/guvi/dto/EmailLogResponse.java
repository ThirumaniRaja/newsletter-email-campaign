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
public class EmailLogResponse {
    private Long id;
    private String subscriberEmail;
    private String subscriberName;
    private String status;
    private String errorMessage;
    private LocalDateTime createdAt;
}

