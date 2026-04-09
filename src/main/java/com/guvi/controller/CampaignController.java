package com.guvi.controller;

import com.guvi.dto.CampaignRequest;
import com.guvi.dto.CampaignResponse;
import com.guvi.dto.ScheduleCampaignRequest;
import com.guvi.dto.EmailLogResponse;
import com.guvi.dto.ApiResponse;
import com.guvi.entity.User;
import com.guvi.service.CampaignService;
import com.guvi.scheduler.CampaignScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CampaignScheduler campaignScheduler;

    @PostMapping
    public ResponseEntity<ApiResponse<CampaignResponse>> createCampaign(
            @RequestBody CampaignRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        CampaignResponse response = campaignService.createCampaign(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Campaign created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignResponse>> getCampaign(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        CampaignResponse response = campaignService.getCampaignById(id, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Campaign retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CampaignResponse>>> getAllCampaigns(
            Authentication authentication,
            Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        Page<CampaignResponse> response = campaignService.getAllCampaigns(user, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Campaigns retrieved successfully"));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<CampaignResponse>>> getCampaignsByStatus(
            @PathVariable String status,
            Authentication authentication,
            Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        Page<CampaignResponse> response = campaignService.getCampaignsByStatus(user, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Campaigns retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignResponse>> updateCampaign(
            @PathVariable Long id,
            @RequestBody CampaignRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        CampaignResponse response = campaignService.updateCampaign(id, request, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Campaign updated successfully"));
    }

    @PostMapping("/{id}/schedule")
    public ResponseEntity<ApiResponse<CampaignResponse>> scheduleCampaign(
            @PathVariable Long id,
            @RequestBody ScheduleCampaignRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        CampaignResponse response = campaignService.scheduleCampaign(id, request.getScheduledFor(), user);
        return ResponseEntity.ok(ApiResponse.success(response, "Campaign scheduled successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCampaign(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        campaignService.deleteCampaign(id, user);
        return ResponseEntity.ok(ApiResponse.success(null, "Campaign deleted successfully"));
    }

    @GetMapping("/{id}/email-logs")
    public ResponseEntity<ApiResponse<List<EmailLogResponse>>> getEmailLogs(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<EmailLogResponse> response = campaignService.getEmailLogs(id, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Email logs retrieved successfully"));
    }

    // ─────────────────────────────────────────────────────────────
    //  DEBUG / SIMULATION endpoints
    //  Use these to manually trigger email sending without waiting
    //  for the scheduler tick. Do NOT expose in production.
    // ─────────────────────────────────────────────────────────────

    /**
     * Manually send a specific SCHEDULED campaign right now.
     * Useful for testing a single campaign without waiting 60s.
     *
     * POST /api/campaigns/{id}/trigger
     */
    @PostMapping("/{id}/trigger")
    public ResponseEntity<ApiResponse<CampaignResponse>> triggerCampaign(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        CampaignResponse response = campaignService.triggerCampaignNow(id, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Campaign triggered and sent (mock)"));
    }

    /**
     * Manually run the scheduler once right now — processes ALL due SCHEDULED campaigns.
     * Equivalent to one scheduler tick firing immediately.
     *
     * POST /api/campaigns/trigger-all
     */
    @PostMapping("/trigger-all")
    public ResponseEntity<ApiResponse<String>> triggerAllDueCampaigns(Authentication authentication) {
        campaignScheduler.processPendingCampaigns();
        return ResponseEntity.ok(ApiResponse.success(
                "Scheduler tick executed — check server logs for [SCHEDULER] and [MOCK] output",
                "Trigger complete"));
    }
}

