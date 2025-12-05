/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.mapper;

import java.time.LocalTime;
import java.util.EnumSet;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.phanikb.rootbytes.dto.v1.response.NotificationPreferenceResponse;
import com.github.phanikb.rootbytes.entity.NotificationPreference;
import com.github.phanikb.rootbytes.enums.notification.NotificationFrequency;
import com.github.phanikb.rootbytes.enums.notification.NotificationType;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationPreferenceMapperTest {

    private NotificationPreferenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new NotificationPreferenceMapper();
    }

    @Test
    void shouldMapPreferenceToResponse() {
        UUID userId = UUID.randomUUID();
        NotificationPreference preference = NotificationPreference.builder()
                .userId(userId)
                .emailEnabled(true)
                .smsEnabled(false)
                .frequency(NotificationFrequency.DAILY_DIGEST)
                .quietHoursStart(LocalTime.of(22, 0))
                .quietHoursEnd(LocalTime.of(8, 0))
                .subscribedEvents(EnumSet.of(NotificationType.GENERAL))
                .build();

        NotificationPreferenceResponse response = mapper.toResponse(preference);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertTrue(response.getEmailEnabled());
        assertFalse(response.getSmsEnabled());
        assertEquals(NotificationFrequency.DAILY_DIGEST, response.getFrequency());
        assertEquals("22:00", response.getQuietHoursStart());
        assertEquals("08:00", response.getQuietHoursEnd());
        assertNotNull(response.getSubscribedEvents());
        assertEquals(1, response.getSubscribedEvents().length);
        assertEquals("GENERAL", response.getSubscribedEvents()[0]);
    }

    @Test
    void shouldHandleNullSubscribedEvents() {
        NotificationPreference preference = NotificationPreference.builder()
                .userId(UUID.randomUUID())
                .emailEnabled(true)
                .smsEnabled(false)
                .frequency(NotificationFrequency.INSTANT)
                .subscribedEvents(null)
                .build();

        NotificationPreferenceResponse response = mapper.toResponse(preference);

        assertNotNull(response);
        assertNotNull(response.getSubscribedEvents());
        assertEquals(0, response.getSubscribedEvents().length);
    }

    @Test
    void shouldHandleEmptySubscribedEvents() {
        NotificationPreference preference = NotificationPreference.builder()
                .userId(UUID.randomUUID())
                .emailEnabled(false)
                .smsEnabled(true)
                .frequency(NotificationFrequency.WEEKLY_DIGEST)
                .subscribedEvents(EnumSet.noneOf(NotificationType.class))
                .build();

        NotificationPreferenceResponse response = mapper.toResponse(preference);

        assertNotNull(response);
        assertArrayEquals(new String[0], response.getSubscribedEvents());
    }

    @Test
    void shouldHandleNullQuietHours() {
        NotificationPreference preference = NotificationPreference.builder()
                .userId(UUID.randomUUID())
                .emailEnabled(true)
                .smsEnabled(true)
                .frequency(NotificationFrequency.INSTANT)
                .quietHoursStart(null)
                .quietHoursEnd(null)
                .subscribedEvents(EnumSet.noneOf(NotificationType.class))
                .build();

        NotificationPreferenceResponse response = mapper.toResponse(preference);

        assertNotNull(response);
        assertNull(response.getQuietHoursStart());
        assertNull(response.getQuietHoursEnd());
    }

    @Test
    void shouldMapMultipleSubscribedEvents() {
        NotificationPreference preference = NotificationPreference.builder()
                .userId(UUID.randomUUID())
                .emailEnabled(true)
                .smsEnabled(false)
                .frequency(NotificationFrequency.DAILY_DIGEST)
                .subscribedEvents(EnumSet.of(NotificationType.GENERAL, NotificationType.SECURITY_ALERT))
                .build();

        NotificationPreferenceResponse response = mapper.toResponse(preference);

        assertNotNull(response);
        assertEquals(2, response.getSubscribedEvents().length);
    }
}
