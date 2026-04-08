package com.guvi.controller;

import com.guvi.dto.MailingListRequest;
import com.guvi.dto.MailingListResponse;
import com.guvi.dto.SubscriberRequest;
import com.guvi.dto.SubscriberResponse;
import com.guvi.dto.ApiResponse;
import com.guvi.entity.User;
import com.guvi.service.MailingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mailing-lists")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MailingListController {

    @Autowired
    private MailingListService mailingListService;

    @PostMapping
    public ResponseEntity<ApiResponse<MailingListResponse>> createMailingList(
            @RequestBody MailingListRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        MailingListResponse response = mailingListService.createMailingList(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Mailing list created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MailingListResponse>> getMailingList(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        MailingListResponse response = mailingListService.getMailingListById(id, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Mailing list retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MailingListResponse>>> getAllMailingLists(
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<MailingListResponse> response = mailingListService.getAllMailingLists(user);
        return ResponseEntity.ok(ApiResponse.success(response, "Mailing lists retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MailingListResponse>> updateMailingList(
            @PathVariable Long id,
            @RequestBody MailingListRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        MailingListResponse response = mailingListService.updateMailingList(id, request, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Mailing list updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMailingList(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        mailingListService.deleteMailingList(id, user);
        return ResponseEntity.ok(ApiResponse.success(null, "Mailing list deleted successfully"));
    }

    @PostMapping("/{id}/subscribers")
    public ResponseEntity<ApiResponse<SubscriberResponse>> addSubscriber(
            @PathVariable Long id,
            @RequestBody SubscriberRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        SubscriberResponse response = mailingListService.addSubscriber(id, request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Subscriber added successfully"));
    }

    @GetMapping("/{mailingListId}/subscribers/{subscriberId}")
    public ResponseEntity<ApiResponse<SubscriberResponse>> getSubscriber(
            @PathVariable Long mailingListId,
            @PathVariable Long subscriberId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        SubscriberResponse response = mailingListService.getSubscriber(mailingListId, subscriberId, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Subscriber retrieved successfully"));
    }

    @GetMapping("/{id}/subscribers")
    public ResponseEntity<ApiResponse<List<SubscriberResponse>>> getSubscribers(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<SubscriberResponse> response = mailingListService.getSubscribers(id, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Subscribers retrieved successfully"));
    }

    @DeleteMapping("/{mailingListId}/subscribers/{subscriberId}")
    public ResponseEntity<ApiResponse<Void>> removeSubscriber(
            @PathVariable Long mailingListId,
            @PathVariable Long subscriberId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        mailingListService.removeSubscriber(mailingListId, subscriberId, user);
        return ResponseEntity.ok(ApiResponse.success(null, "Subscriber removed successfully"));
    }
}

