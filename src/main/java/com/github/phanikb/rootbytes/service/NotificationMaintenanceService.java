/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.function.IntConsumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.config.NotificationArchiveProperties;
import com.github.phanikb.rootbytes.dto.v1.response.NotificationCountResponse;
import com.github.phanikb.rootbytes.enums.notification.NotificationStatus;
import com.github.phanikb.rootbytes.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationMaintenanceService {

    private final NotificationRepository notificationRepository;
    private final NotificationQueueService notificationQueueService;
    private final NotificationArchiveProperties notificationArchiveProperties;

    @Transactional
    public NotificationCountResponse markAllAsRead(UUID userId) {
        Instant now = Instant.now();
        int updated = notificationRepository.markAllAsReadForUser(userId, now);
        log.info("Marked {} notifications as read for user {}", updated, userId);
        return buildNotificationCount(userId);
    }

    @Transactional(readOnly = true)
    public NotificationCountResponse getNotificationCounts(UUID userId) {
        return buildNotificationCount(userId);
    }

    private NotificationCountResponse buildNotificationCount(UUID userId) {
        applyArchivePolicies();

        long internalUnread = notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
        long externalPending = notificationQueueService.countActiveForUser(userId);

        return NotificationCountResponse.builder()
                .internalUnread(internalUnread)
                .externalPending(externalPending)
                .total(internalUnread + externalPending)
                .build();
    }

    private void applyArchivePolicies() {
        Instant now = Instant.now();

        applyPolicy(
                notificationArchiveProperties.getReadAfterDays(),
                days -> notificationRepository.archiveReadNotifications(now.minus(days, ChronoUnit.DAYS), now));

        applyPolicy(
                notificationArchiveProperties.getDeleteAfterDays(),
                days -> notificationRepository.deleteArchivedNotifications(now.minus(days, ChronoUnit.DAYS)));
    }

    private void applyPolicy(int days, IntConsumer action) {
        if (days > 0) {
            action.accept(days);
        }
    }
}
