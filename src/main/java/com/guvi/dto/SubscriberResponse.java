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
public class SubscriberResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
}

