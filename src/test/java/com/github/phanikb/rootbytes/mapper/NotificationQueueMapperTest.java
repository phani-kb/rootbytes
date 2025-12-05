/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.mapper;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.phanikb.rootbytes.dto.v1.response.NotificationQueueResponse;
import com.github.phanikb.rootbytes.entity.NotificationQueue;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.notification.NotificationChannel;
import com.github.phanikb.rootbytes.enums.notification.NotificationPriority;
import com.github.phanikb.rootbytes.enums.notification.NotificationType;
import com.github.phanikb.rootbytes.enums.notification.QueueStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class NotificationQueueMapperTest {

    private NotificationQueueMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new NotificationQueueMapper();
    }

    @Test
    void shouldMapEntityToResponse() {
        UUID queueId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();
        Instant scheduledFor = now.plusSeconds(3600);

        UserEntity user = UserEntity.builder()
                .id(userId)
                .email("test@example.com")
                .lastName("Test")
                .uniqueName("TEST01")
                .build();

        NotificationQueue entity = NotificationQueue.builder()
                .id(queueId)
                .user(user)
                .notificationType(NotificationType.GENERAL)
                .title("Test Notification")
                .message("This is a test message")
                .data("{\"key\": \"value\"}")
                .actionUrl("https://example.com/action")
                .priority(NotificationPriority.HIGH)
                .channel(NotificationChannel.EMAIL)
                .status(QueueStatus.PENDING)
                .scheduledFor(scheduledFor)
                .createdAt(now)
                .processedAt(null)
                .build();

        NotificationQueueResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(queueId, response.getId());
        assertEquals(userId, response.getUserId());
        assertEquals(NotificationType.GENERAL, response.getNotificationType());
        assertEquals("Test Notification", response.getTitle());
        assertEquals("This is a test message", response.getMessage());
        assertEquals("{\"key\": \"value\"}", response.getData());
        assertEquals("https://example.com/action", response.getActionUrl());
        assertEquals(NotificationPriority.HIGH, response.getPriority());
        assertEquals(NotificationChannel.EMAIL, response.getChannel());
        assertEquals(QueueStatus.PENDING, response.getStatus());
        assertEquals(scheduledFor, response.getScheduledFor());
        assertEquals(now, response.getCreatedAt());
        assertNull(response.getProcessedAt());
    }

    @Test
    void shouldMapEntityWithProcessedAt() {
        UUID queueId = UUID.randomUUID();
        Instant now = Instant.now();
        Instant processedAt = now.plusSeconds(60);

        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("user@example.com")
                .lastName("User")
                .uniqueName("USER01")
                .build();

        NotificationQueue entity = NotificationQueue.builder()
                .id(queueId)
                .user(user)
                .notificationType(NotificationType.SECURITY_ALERT)
                .title("Security Alert")
                .message("Suspicious login detected")
                .priority(NotificationPriority.CRITICAL)
                .channel(NotificationChannel.EMAIL)
                .status(QueueStatus.SENT)
                .scheduledFor(now)
                .createdAt(now)
                .processedAt(processedAt)
                .build();

        NotificationQueueResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertEquals(QueueStatus.SENT, response.getStatus());
        assertEquals(processedAt, response.getProcessedAt());
    }

    @Test
    void shouldMapEntityWithNullOptionalFields() {
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("user@example.com")
                .lastName("User")
                .uniqueName("USER01")
                .build();

        NotificationQueue entity = NotificationQueue.builder()
                .id(UUID.randomUUID())
                .user(user)
                .notificationType(NotificationType.GENERAL)
                .title("Simple Notification")
                .message("Simple message")
                .data(null)
                .actionUrl(null)
                .priority(NotificationPriority.LOW)
                .channel(NotificationChannel.IN_APP)
                .status(QueueStatus.PENDING)
                .scheduledFor(Instant.now())
                .createdAt(Instant.now())
                .build();

        NotificationQueueResponse response = mapper.toResponse(entity);

        assertNotNull(response);
        assertNull(response.getData());
        assertNull(response.getActionUrl());
    }
}
