package com.guvi.service;

import com.guvi.dto.CampaignRequest;
import com.guvi.dto.CampaignResponse;
import com.guvi.dto.EmailLogResponse;
import com.guvi.entity.Campaign;
import com.guvi.entity.Campaign.CampaignStatus;
import com.guvi.entity.MailingList;
import com.guvi.entity.User;
import com.guvi.exception.InvalidScheduleTimeException;
import com.guvi.exception.ResourceNotFoundException;
import com.guvi.repository.CampaignRepository;
import com.guvi.repository.MailingListRepository;
import com.guvi.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private MailingListRepository mailingListRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private EmailService emailService;

    public CampaignResponse createCampaign(CampaignRequest request, User user) {
        MailingList mailingList = mailingListRepository.findByIdAndUser(request.getMailingListId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found"));

        Campaign campaign = Campaign.builder()
                .name(request.getName())
                .subject(request.getSubject())
                .content(request.getContent())
                .status(CampaignStatus.DRAFT)
                .mailingList(mailingList)
                .user(user)
                .build();

        Campaign savedCampaign = campaignRepository.save(campaign);
        return convertToResponse(savedCampaign);
    }

    public CampaignResponse getCampaignById(Long id, User user) {
        Campaign campaign = campaignRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        return convertToResponse(campaign);
    }

    public Page<CampaignResponse> getAllCampaigns(User user, Pageable pageable) {
        Page<Campaign> campaigns = campaignRepository.findByUser(user, pageable);
        return campaigns.map(this::convertToResponse);
    }

    public Page<CampaignResponse> getCampaignsByStatus(User user, String status, Pageable pageable) {
        try {
            CampaignStatus campaignStatus = CampaignStatus.valueOf(status.toUpperCase());
            Page<Campaign> campaigns = campaignRepository.findByUserAndStatus(user, campaignStatus, pageable);
            return campaigns.map(this::convertToResponse);
        } catch (IllegalArgumentException e) {
            throw new InvalidScheduleTimeException("Invalid campaign status: " + status);
        }
    }

    public CampaignResponse updateCampaign(Long id, CampaignRequest request, User user) {
        Campaign campaign = campaignRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        if (!campaign.getStatus().equals(CampaignStatus.DRAFT)) {
            throw new InvalidScheduleTimeException("Can only edit campaigns in DRAFT status");
        }

        MailingList mailingList = mailingListRepository.findByIdAndUser(request.getMailingListId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found"));

        campaign.setName(request.getName());
        campaign.setSubject(request.getSubject());
        campaign.setContent(request.getContent());
        campaign.setMailingList(mailingList);

        Campaign updatedCampaign = campaignRepository.save(campaign);
        return convertToResponse(updatedCampaign);
    }

    public CampaignResponse scheduleCampaign(Long id, LocalDateTime scheduledFor, User user) {
        Campaign campaign = campaignRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        if (scheduledFor.isBefore(LocalDateTime.now())) {
            throw new InvalidScheduleTimeException("Scheduled time cannot be in the past");
        }

        campaign.setStatus(CampaignStatus.SCHEDULED);
        campaign.setScheduledFor(scheduledFor);

        Campaign updatedCampaign = campaignRepository.save(campaign);
        return convertToResponse(updatedCampaign);
    }

    public void deleteCampaign(Long id, User user) {
        Campaign campaign = campaignRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        if (!campaign.getStatus().equals(CampaignStatus.DRAFT)) {
            throw new InvalidScheduleTimeException("Can only delete campaigns in DRAFT status");
        }

        campaignRepository.delete(campaign);
    }

    public void processPendingCampaigns() {
        LocalDateTime now = LocalDateTime.now();
        List<Campaign> pendingCampaigns = campaignRepository
                .findByStatusAndScheduledForLessThanEqual(CampaignStatus.SCHEDULED, now);

        for (Campaign campaign : pendingCampaigns) {
            sendCampaign(campaign);
        }
    }

    public void sendCampaign(Campaign campaign) {
        emailService.sendCampaign(campaign);
        campaign.setStatus(CampaignStatus.SENT);
        campaign.setSentAt(LocalDateTime.now());
        campaignRepository.save(campaign);
    }

    public List<EmailLogResponse> getEmailLogs(Long campaignId, User user) {
        Campaign campaign = campaignRepository.findByIdAndUser(campaignId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        return emailService.getEmailLogs(campaign)
                .stream()
                .map(this::convertEmailLogToResponse)
                .collect(Collectors.toList());
    }

    private CampaignResponse convertToResponse(Campaign campaign) {
        return CampaignResponse.builder()
                .id(campaign.getId())
                .name(campaign.getName())
                .subject(campaign.getSubject())
                .content(campaign.getContent())
                .status(campaign.getStatus().toString())
                .mailingListId(campaign.getMailingList().getId())
                .mailingListName(campaign.getMailingList().getName())
                .scheduledFor(campaign.getScheduledFor())
                .sentAt(campaign.getSentAt())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .totalSubscribers(campaign.getMailingList().getSubscribers().size())
                .build();
    }

    private EmailLogResponse convertEmailLogToResponse(com.guvi.entity.EmailLog emailLog) {
        return EmailLogResponse.builder()
                .id(emailLog.getId())
                .subscriberEmail(emailLog.getSubscriberEmail())
                .subscriberName(emailLog.getSubscriberName())
                .status(emailLog.getStatus().toString())
                .errorMessage(emailLog.getErrorMessage())
                .createdAt(emailLog.getCreatedAt())
                .build();
    }
}

