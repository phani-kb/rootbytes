/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.enums.notification.NotificationChannel;
import com.github.phanikb.rootbytes.enums.notification.NotificationPriority;
import com.github.phanikb.rootbytes.enums.notification.NotificationType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationQueueRequest {

    @NotNull(message = "User id is required")
    private UUID userId;

    @NotNull(message = "Notification type is required")
    private NotificationType notificationType;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    private String data;

    @Size(max = 500, message = "Action URL must not exceed 500 characters")
    private String actionUrl;

    private NotificationPriority priority;

    @NotNull(message = "Channel is required")
    private NotificationChannel channel;

    private Instant scheduledFor;
}
