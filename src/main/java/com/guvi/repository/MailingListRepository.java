package com.guvi.repository;

import com.guvi.entity.MailingList;
import com.guvi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MailingListRepository extends JpaRepository<MailingList, Long> {
    List<MailingList> findByUser(User user);
    Optional<MailingList> findByIdAndUser(Long id, User user);
    boolean existsByNameAndUser(String name, User user);
}

