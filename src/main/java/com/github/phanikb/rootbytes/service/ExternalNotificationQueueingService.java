/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.config.NotificationQueueProperties;
import com.github.phanikb.rootbytes.dto.v1.request.NotificationQueueRequest;
import com.github.phanikb.rootbytes.entity.Notification;
import com.github.phanikb.rootbytes.entity.NotificationPreference;
import com.github.phanikb.rootbytes.enums.notification.NotificationChannel;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalNotificationQueueingService {

    private final NotificationQueueService notificationQueueService;
    private final NotificationQueueProperties notificationQueueProperties;
    private final NotificationSchedulingService schedulingService;

    public void queueExternalNotification(Notification notification, NotificationPreference preference) {
        if (!notification.getType().isExternal()) {
            return;
        }

        if (!notificationQueueProperties.isEnabled()) {
            log.debug(
                    "Notification queue disabled; skipping external notification for user {}",
                    notification.getUser().getEmail());
            return;
        }

        NotificationChannel channel = schedulingService.determineChannel(preference);
        Instant scheduledFor = schedulingService.calculateScheduledTime(preference.getFrequency());

        NotificationQueueRequest queueRequest = NotificationQueueRequest.builder()
                .userId(notification.getUser().getId())
                .notificationType(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .data(notification.getData())
                .actionUrl(notification.getActionUrl())
                .priority(notification.getPriority())
                .channel(channel)
                .scheduledFor(scheduledFor)
                .build();

        notificationQueueService.enqueue(queueRequest);

        log.info(
                "External notification queued: type={}, user={}, channel={}, scheduledFor={}, requiredAction={}",
                notification.getType(),
                notification.getUser().getEmail(),
                channel,
                scheduledFor,
                notification.getType().isRequiresAction());
    }
}
