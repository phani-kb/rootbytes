/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.response;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

import com.github.phanikb.rootbytes.enums.notification.NotificationChannel;
import com.github.phanikb.rootbytes.enums.notification.NotificationPriority;
import com.github.phanikb.rootbytes.enums.notification.NotificationType;
import com.github.phanikb.rootbytes.enums.notification.QueueStatus;

@Value
@Builder
public class NotificationQueueResponse {
    UUID id;
    UUID userId;
    NotificationType notificationType;
    String title;
    String message;
    String data;
    String actionUrl;
    NotificationPriority priority;
    NotificationChannel channel;
    QueueStatus status;
    Instant scheduledFor;
    Instant createdAt;
    Instant processedAt;
}
