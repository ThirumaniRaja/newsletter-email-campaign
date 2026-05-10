package com.guvi.entity;

import com.guvi.entity.Campaign.CampaignStatus;
import com.guvi.entity.EmailLog.EmailStatus;
import com.guvi.repository.CampaignRepository;
import com.guvi.repository.EmailLogRepository;
import com.guvi.repository.MailingListRepository;
import com.guvi.repository.SubscriberRepository;
import com.guvi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class EntityMappingDataJpaTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MailingListRepository mailingListRepository;

	@Autowired
	private SubscriberRepository subscriberRepository;

	@Autowired
	private CampaignRepository campaignRepository;

	@Autowired
	private EmailLogRepository emailLogRepository;

	@Test
	void shouldPersistAndResolveAllEntityRelationships() {
		User user = userRepository.save(User.builder()
				.username("user1")
				.email("user1@example.com")
				.password("secret")
				.build());

		MailingList mailingList = mailingListRepository.save(MailingList.builder()
				.name("Weekly News")
				.description("Weekly updates")
				.user(user)
				.build());

		Subscriber subscriber = subscriberRepository.save(Subscriber.builder()
				.name("Alice")
				.email("alice@example.com")
				.mailingList(mailingList)
				.build());

		Campaign campaign = campaignRepository.save(Campaign.builder()
				.name("May Campaign")
				.subject("May News")
				.content("Hello subscribers")
				.status(CampaignStatus.DRAFT)
				.mailingList(mailingList)
				.user(user)
				.scheduledFor(LocalDateTime.now().plusHours(1))
				.build());

		EmailLog emailLog = emailLogRepository.save(EmailLog.builder()
				.campaign(campaign)
				.subscriberEmail(subscriber.getEmail())
				.subscriberName(subscriber.getName())
				.status(EmailStatus.SENT)
				.build());

		assertTrue(mailingListRepository.findByIdAndUser(mailingList.getId(), user).isPresent());
		assertTrue(subscriberRepository.findByEmailAndMailingList(subscriber.getEmail(), mailingList).isPresent());
		assertTrue(campaignRepository.findByIdAndUser(campaign.getId(), user).isPresent());

		List<EmailLog> logs = emailLogRepository.findByCampaign(campaign);
		assertEquals(1, logs.size());
		assertEquals(emailLog.getId(), logs.get(0).getId());
	}

	@Test
	void shouldEnforceUniqueMailingListNamePerUser() {
		User user = userRepository.save(User.builder()
				.username("user2")
				.email("user2@example.com")
				.password("secret")
				.build());

		mailingListRepository.saveAndFlush(MailingList.builder()
				.name("Offers")
				.description("Latest offers")
				.user(user)
				.build());

		assertThrows(DataIntegrityViolationException.class, () ->
				mailingListRepository.saveAndFlush(MailingList.builder()
						.name("Offers")
						.description("Duplicate name")
						.user(user)
						.build()));
	}

	@Test
	void shouldEnforceUniqueSubscriberEmailPerMailingList() {
		User user = userRepository.save(User.builder()
				.username("user3")
				.email("user3@example.com")
				.password("secret")
				.build());

		MailingList mailingList = mailingListRepository.save(MailingList.builder()
				.name("Product News")
				.description("Product announcements")
				.user(user)
				.build());

		subscriberRepository.saveAndFlush(Subscriber.builder()
				.name("Bob")
				.email("bob@example.com")
				.mailingList(mailingList)
				.build());

		assertThrows(DataIntegrityViolationException.class, () ->
				subscriberRepository.saveAndFlush(Subscriber.builder()
						.name("Bobby")
						.email("bob@example.com")
						.mailingList(mailingList)
						.build()));
	}
}


