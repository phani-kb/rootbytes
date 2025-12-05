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

import com.github.phanikb.rootbytes.dto.v1.response.NotificationCountResponse;
import com.github.phanikb.rootbytes.service.NotificationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    private NotificationController controller;
    private UUID userId;
    private NotificationCountResponse countResponse;

    @BeforeEach
    void setUp() {
        controller = new NotificationController(notificationService);
        userId = UUID.randomUUID();
        countResponse = NotificationCountResponse.builder()
                .internalUnread(5)
                .externalPending(3)
                .total(8)
                .build();
    }

    @Test
    void shouldGetNotificationCounts() {
        when(notificationService.getNotificationCounts(userId)).thenReturn(countResponse);

        var result = controller.getNotificationCounts(userId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(5, result.getBody().getInternalUnread());
        assertEquals(3, result.getBody().getExternalPending());
        assertEquals(8, result.getBody().getTotal());
    }

    @Test
    void shouldMarkAllAsRead() {
        var readResponse = NotificationCountResponse.builder()
                .internalUnread(0)
                .externalPending(0)
                .total(0)
                .build();
        when(notificationService.markAllAsRead(userId)).thenReturn(readResponse);

        var result = controller.markAllAsRead(userId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertEquals(0, result.getBody().getInternalUnread());
    }
}
