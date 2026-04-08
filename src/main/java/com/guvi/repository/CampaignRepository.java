package com.guvi.repository;

import com.guvi.entity.Campaign;
import com.guvi.entity.Campaign.CampaignStatus;
import com.guvi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    Page<Campaign> findByUser(User user, Pageable pageable);
    Page<Campaign> findByUserAndStatus(User user, CampaignStatus status, Pageable pageable);
    Optional<Campaign> findByIdAndUser(Long id, User user);
    List<Campaign> findByStatusAndScheduledForLessThanEqual(CampaignStatus status, LocalDateTime dateTime);
}

