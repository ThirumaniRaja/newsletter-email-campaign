package com.guvi.controller;

import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CampaignControllerValidationAnnotationTest {

	@Test
	void createCampaignRequestBodyMustBeValidated() throws NoSuchMethodException {
		Method method = CampaignController.class.getMethod("createCampaign",
				com.guvi.dto.CampaignRequest.class,
				Authentication.class);

		assertTrue(hasAnnotation(method.getParameterAnnotations()[0], Valid.class));
		assertTrue(hasAnnotation(method.getParameterAnnotations()[0], RequestBody.class));
	}

	@Test
	void updateCampaignRequestBodyMustBeValidated() throws NoSuchMethodException {
		Method method = CampaignController.class.getMethod("updateCampaign",
				Long.class,
				com.guvi.dto.CampaignRequest.class,
				Authentication.class);

		assertTrue(hasAnnotation(method.getParameterAnnotations()[1], Valid.class));
		assertTrue(hasAnnotation(method.getParameterAnnotations()[1], RequestBody.class));
	}

	@Test
	void scheduleCampaignRequestBodyMustBeValidated() throws NoSuchMethodException {
		Method method = CampaignController.class.getMethod("scheduleCampaign",
				Long.class,
				com.guvi.dto.ScheduleCampaignRequest.class,
				Authentication.class);

		assertTrue(hasAnnotation(method.getParameterAnnotations()[1], Valid.class));
		assertTrue(hasAnnotation(method.getParameterAnnotations()[1], RequestBody.class));
	}

	private boolean hasAnnotation(Annotation[] annotations, Class<? extends Annotation> expected) {
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().equals(expected)) {
				return true;
			}
		}
		return false;
	}
}

