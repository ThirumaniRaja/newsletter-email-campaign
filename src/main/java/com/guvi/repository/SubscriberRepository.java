package com.guvi.repository;

import com.guvi.entity.MailingList;
import com.guvi.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    List<Subscriber> findByMailingList(MailingList mailingList);
    Optional<Subscriber> findByEmailAndMailingList(String email, MailingList mailingList);
    boolean existsByEmailAndMailingList(String email, MailingList mailingList);
}

