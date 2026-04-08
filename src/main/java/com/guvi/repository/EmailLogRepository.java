package com.guvi.repository;

import com.guvi.entity.Campaign;
import com.guvi.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    List<EmailLog> findByCampaign(Campaign campaign);
}

