package com.guvi.service;

import com.guvi.dto.MailingListRequest;
import com.guvi.dto.MailingListResponse;
import com.guvi.dto.SubscriberRequest;
import com.guvi.dto.SubscriberResponse;
import com.guvi.entity.MailingList;
import com.guvi.entity.Subscriber;
import com.guvi.entity.User;
import com.guvi.exception.InvalidEmailException;
import com.guvi.exception.ResourceNotFoundException;
import com.guvi.repository.MailingListRepository;
import com.guvi.repository.SubscriberRepository;
import com.guvi.util.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MailingListService {

    @Autowired
    private MailingListRepository mailingListRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    public MailingListResponse createMailingList(MailingListRequest request, User user) {
        if (mailingListRepository.existsByNameAndUser(request.getName(), user)) {
            throw new InvalidEmailException("Mailing list with this name already exists");
        }

        MailingList mailingList = MailingList.builder()
                .name(request.getName())
                .description(request.getDescription())
                .user(user)
                .build();

        MailingList savedList = mailingListRepository.save(mailingList);
        return convertToResponse(savedList);
    }

    public MailingListResponse getMailingListById(Long id, User user) {
        MailingList mailingList = mailingListRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found"));
        return convertToResponse(mailingList);
    }

    public List<MailingListResponse> getAllMailingLists(User user) {
        List<MailingList> lists = mailingListRepository.findByUser(user);
        return lists.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public MailingListResponse updateMailingList(Long id, MailingListRequest request, User user) {
        MailingList mailingList = mailingListRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found"));

        mailingList.setName(request.getName());
        mailingList.setDescription(request.getDescription());

        MailingList updatedList = mailingListRepository.save(mailingList);
        return convertToResponse(updatedList);
    }

    public void deleteMailingList(Long id, User user) {
        MailingList mailingList = mailingListRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found"));
        mailingListRepository.delete(mailingList);
    }

    public SubscriberResponse addSubscriber(Long mailingListId, SubscriberRequest request, User user) {
        MailingList mailingList = mailingListRepository.findByIdAndUser(mailingListId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found"));

        // Validate email
        EmailValidator.validate(request.getEmail());

        // Check for duplicates
        if (subscriberRepository.existsByEmailAndMailingList(request.getEmail(), mailingList)) {
            throw new InvalidEmailException("Email already exists in this mailing list");
        }

        Subscriber subscriber = Subscriber.builder()
                .name(request.getName())
                .email(request.getEmail())
                .mailingList(mailingList)
                .build();

        Subscriber savedSubscriber = subscriberRepository.save(subscriber);
        return convertSubscriberToResponse(savedSubscriber);
    }

    public void removeSubscriber(Long mailingListId, Long subscriberId, User user) {
        MailingList mailingList = mailingListRepository.findByIdAndUser(mailingListId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found"));

        Subscriber subscriber = subscriberRepository.findById(subscriberId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found"));

        if (!subscriber.getMailingList().getId().equals(mailingListId)) {
            throw new ResourceNotFoundException("Subscriber not found in this mailing list");
        }

        subscriberRepository.delete(subscriber);
    }

    public SubscriberResponse getSubscriber(Long mailingListId, Long subscriberId, User user) {
        MailingList mailingList = mailingListRepository.findByIdAndUser(mailingListId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found"));

        Subscriber subscriber = subscriberRepository.findById(subscriberId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found"));

        if (!subscriber.getMailingList().getId().equals(mailingListId)) {
            throw new ResourceNotFoundException("Subscriber not found in this mailing list");
        }

        return convertSubscriberToResponse(subscriber);
    }

    public List<SubscriberResponse> getSubscribers(Long mailingListId, User user) {
        MailingList mailingList = mailingListRepository.findByIdAndUser(mailingListId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found"));

        return subscriberRepository.findByMailingList(mailingList)
                .stream()
                .map(this::convertSubscriberToResponse)
                .collect(Collectors.toList());
    }

    private MailingListResponse convertToResponse(MailingList mailingList) {
        List<Subscriber> rawSubscribers = mailingList.getSubscribers() != null
                ? mailingList.getSubscribers()
                : java.util.Collections.emptyList();

        List<SubscriberResponse> subscribers = rawSubscribers.stream()
                .map(this::convertSubscriberToResponse)
                .collect(Collectors.toList());

        return MailingListResponse.builder()
                .id(mailingList.getId())
                .name(mailingList.getName())
                .description(mailingList.getDescription())
                .subscriberCount(rawSubscribers.size())
                .createdAt(mailingList.getCreatedAt())
                .updatedAt(mailingList.getUpdatedAt())
                .subscribers(subscribers)
                .build();
    }

    private SubscriberResponse convertSubscriberToResponse(Subscriber subscriber) {
        return SubscriberResponse.builder()
                .id(subscriber.getId())
                .name(subscriber.getName())
                .email(subscriber.getEmail())
                .createdAt(subscriber.getCreatedAt())
                .build();
    }
}

