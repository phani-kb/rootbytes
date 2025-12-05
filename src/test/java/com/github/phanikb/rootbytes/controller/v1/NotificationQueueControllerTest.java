/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.phanikb.rootbytes.dto.v1.request.NotificationQueueRequest;
import com.github.phanikb.rootbytes.dto.v1.response.NotificationQueueResponse;
import com.github.phanikb.rootbytes.enums.notification.NotificationChannel;
import com.github.phanikb.rootbytes.enums.notification.NotificationPriority;
import com.github.phanikb.rootbytes.enums.notification.NotificationType;
import com.github.phanikb.rootbytes.enums.notification.QueueStatus;
import com.github.phanikb.rootbytes.service.NotificationQueueService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationQueueControllerTest {

    @Mock
    private NotificationQueueService queueService;

    private NotificationQueueController controller;
    private UUID userId;
    private NotificationQueueResponse response;

    @BeforeEach
    void setUp() {
        controller = new NotificationQueueController(queueService);
        userId = UUID.randomUUID();
        response = NotificationQueueResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .notificationType(NotificationType.GENERAL)
                .title("Test Notification")
                .message("Test message")
                .priority(NotificationPriority.MEDIUM)
                .channel(NotificationChannel.EMAIL)
                .status(QueueStatus.PENDING)
                .scheduledFor(Instant.now())
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void shouldEnqueue() {
        var request = NotificationQueueRequest.builder()
                .userId(userId)
                .notificationType(NotificationType.GENERAL)
                .title("Test Notification")
                .message("Test message")
                .priority(NotificationPriority.MEDIUM)
                .channel(NotificationChannel.EMAIL)
                .build();
        when(queueService.enqueue(any(NotificationQueueRequest.class))).thenReturn(response);

        var result = controller.enqueue(request);

        assertEquals(201, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(response, result.getBody().getData());
    }

    @Test
    void shouldGetQueueForUser() {
        when(queueService.getQueueForUser(userId, null)).thenReturn(Collections.singletonList(response));

        var result = controller.getQueueForUser(userId, null);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertNotNull(result.getBody().getData());
        assertEquals(1, result.getBody().getData().size());
    }

    @Test
    void shouldGetQueueForUserWithStatus() {
        when(queueService.getQueueForUser(userId, QueueStatus.PENDING)).thenReturn(Collections.singletonList(response));

        var result = controller.getQueueForUser(userId, QueueStatus.PENDING);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
    }

    @Test
    void shouldGetDueNotifications() {
        when(queueService.getDueNotifications(null)).thenReturn(List.of(response));

        var result = controller.getDueNotifications(null);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
    }

    @Test
    void shouldGetDueNotificationsWithChannel() {
        when(queueService.getDueNotifications(NotificationChannel.EMAIL)).thenReturn(List.of(response));

        var result = controller.getDueNotifications(NotificationChannel.EMAIL);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
    }
}
