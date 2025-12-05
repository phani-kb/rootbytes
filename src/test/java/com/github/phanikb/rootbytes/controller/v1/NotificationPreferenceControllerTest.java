/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.phanikb.rootbytes.dto.v1.request.NotificationPreferenceRequest;
import com.github.phanikb.rootbytes.dto.v1.response.NotificationPreferenceResponse;
import com.github.phanikb.rootbytes.enums.notification.NotificationFrequency;
import com.github.phanikb.rootbytes.service.NotificationPreferenceService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationPreferenceControllerTest {

    @Mock
    private NotificationPreferenceService notificationPreferenceService;

    private NotificationPreferenceController controller;
    private UUID userId;
    private NotificationPreferenceResponse response;

    @BeforeEach
    void setUp() {
        controller = new NotificationPreferenceController(notificationPreferenceService);
        userId = UUID.randomUUID();
        response = NotificationPreferenceResponse.builder()
                .userId(userId)
                .emailEnabled(true)
                .smsEnabled(false)
                .frequency(NotificationFrequency.DAILY_DIGEST)
                .quietHoursStart("22:00")
                .quietHoursEnd("08:00")
                .subscribedEvents(new String[] {"GENERAL"})
                .build();
    }

    @Test
    void shouldGetPreferences() {
        when(notificationPreferenceService.getPreferences(userId)).thenReturn(response);

        var result = controller.getPreferences(userId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(response, result.getBody().getData());
    }

    @Test
    void shouldUpdatePreferences() {
        var request = NotificationPreferenceRequest.builder()
                .emailEnabled(true)
                .smsEnabled(true)
                .frequency(NotificationFrequency.INSTANT)
                .build();
        when(notificationPreferenceService.updatePreferences(eq(userId), any(NotificationPreferenceRequest.class)))
                .thenReturn(response);

        var result = controller.updatePreferences(userId, request);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(response, result.getBody().getData());
    }
}
