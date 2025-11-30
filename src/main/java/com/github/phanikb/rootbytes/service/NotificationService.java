/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.config.NotificationArchiveProperties;
import com.github.phanikb.rootbytes.dto.v1.request.NotificationRequest;
import com.github.phanikb.rootbytes.dto.v1.response.NotificationCountResponse;
import com.github.phanikb.rootbytes.entity.Notification;
import com.github.phanikb.rootbytes.entity.NotificationMetadata;
import com.github.phanikb.rootbytes.entity.NotificationPreference;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.notification.NotificationPriority;
import com.github.phanikb.rootbytes.enums.notification.NotificationType;
import com.github.phanikb.rootbytes.repository.NotificationMetadataRepository;
import com.github.phanikb.rootbytes.repository.NotificationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMetadataRepository metadataRepository;
    private final NotificationPreferenceService preferenceService;
    private final ExternalNotificationQueueingService externalQueueingService;
    private final NotificationMaintenanceService maintenanceService;
    private final NotificationArchiveProperties notificationArchiveProperties;

    @Transactional
    public Optional<Notification> createNotification(@NotNull NotificationRequest request) {
        UserEntity user = request.getUser();
        NotificationType type = request.getType();

        NotificationPreference preference = null;
        boolean needsPreference = type.isSubscribable() || type.isExternal();
        if (needsPreference) {
            preference = preferenceService.getOrCreatePreference(user);
        }
        if (type.isSubscribable() && !preferenceService.shouldNotify(preference, type)) {
            log.debug("User {} has disabled notifications for type {}", user.getEmail(), type);
            return Optional.empty();
        }

        Notification notification = buildNotification(request);
        Notification savedNotification = notificationRepository.save(notification);

        if (request.getEntityType() != null) {
            createMetadata(savedNotification, request);
        }

        NotificationPreference effectivePreference = Optional.ofNullable(preference)
                .orElseGet(() -> preferenceService.getOrCreatePreference(savedNotification.getUser()));
        externalQueueingService.queueExternalNotification(savedNotification, effectivePreference);

        return Optional.of(savedNotification);
    }

    private Notification buildNotification(NotificationRequest request) {
        Instant expiresAt = request.getType().getPriority() == NotificationPriority.CRITICAL
                ? Instant.now().plus(notificationArchiveProperties.getReadAfterDays(), ChronoUnit.DAYS)
                : Instant.now().plus(notificationArchiveProperties.getDeleteAfterDays(), ChronoUnit.DAYS);

        return Notification.builder()
                .user(request.getUser())
                .type(request.getType())
                .title(request.getTitle())
                .message(request.getMessage())
                .data(request.getData())
                .priority(request.getType().getPriority())
                .actionUrl(request.getActionUrl())
                .expiresAt(expiresAt)
                .build();
    }

    private void createMetadata(Notification notification, NotificationRequest request) {
        NotificationMetadata metadata = NotificationMetadata.builder()
                .notification(notification)
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .actionUrl(request.getActionUrl())
                .build();

        metadataRepository.save(metadata);
    }

    @Transactional
    public NotificationCountResponse markAllAsRead(UUID userId) {
        return maintenanceService.markAllAsRead(userId);
    }

    @Transactional(readOnly = true)
    public NotificationCountResponse getNotificationCounts(UUID userId) {
        return maintenanceService.getNotificationCounts(userId);
    }
}
