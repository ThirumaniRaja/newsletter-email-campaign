package com.guvi.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestDtoValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void campaignRequestShouldFailWhenRequiredFieldsAreMissing() {
        CampaignRequest request = CampaignRequest.builder()
                .name(" ")
                .subject("")
                .content(null)
                .mailingListId(null)
                .build();

        Set<ConstraintViolation<CampaignRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(containsMessage(violations, "Campaign name is required"));
        assertTrue(containsMessage(violations, "Subject is required"));
        assertTrue(containsMessage(violations, "Content is required"));
        assertTrue(containsMessage(violations, "Mailing list id is required"));
    }

    @Test
    void scheduleCampaignRequestShouldFailForNullOrPastDateTime() {
        ScheduleCampaignRequest nullRequest = ScheduleCampaignRequest.builder()
                .scheduledFor(null)
                .build();

        Set<ConstraintViolation<ScheduleCampaignRequest>> nullViolations = validator.validate(nullRequest);
        assertTrue(containsMessage(nullViolations, "Scheduled time is required"));

        ScheduleCampaignRequest pastRequest = ScheduleCampaignRequest.builder()
                .scheduledFor(LocalDateTime.now().minusMinutes(5))
                .build();

        Set<ConstraintViolation<ScheduleCampaignRequest>> pastViolations = validator.validate(pastRequest);
        assertTrue(containsMessage(pastViolations, "Scheduled time must be in the future"));
    }

    private <T> boolean containsMessage(Set<ConstraintViolation<T>> violations, String expectedMessage) {
        return violations.stream().anyMatch(v -> expectedMessage.equals(v.getMessage()));
    }
}

