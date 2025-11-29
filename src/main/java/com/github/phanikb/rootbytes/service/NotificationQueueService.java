/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.config.NotificationQueueProperties;
import com.github.phanikb.rootbytes.dto.v1.request.NotificationQueueRequest;
import com.github.phanikb.rootbytes.dto.v1.response.NotificationQueueResponse;
import com.github.phanikb.rootbytes.entity.NotificationQueue;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.notification.NotificationChannel;
import com.github.phanikb.rootbytes.enums.notification.NotificationPriority;
import com.github.phanikb.rootbytes.enums.notification.QueueStatus;
import com.github.phanikb.rootbytes.exception.NotificationQueueDisabledException;
import com.github.phanikb.rootbytes.exception.NotificationQueueLimitException;
import com.github.phanikb.rootbytes.exception.ResourceNotFoundException;
import com.github.phanikb.rootbytes.mapper.NotificationQueueMapper;
import com.github.phanikb.rootbytes.repository.NotificationQueueRepository;
import com.github.phanikb.rootbytes.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationQueueService {

    private static final NotificationPriority DEFAULT_PRIORITY = NotificationPriority.getDefault();
    private static final Set<QueueStatus> ACTIVE_STATUSES = QueueStatus.activeQueueStatuses();

    private final NotificationQueueRepository queueRepository;
    private final UserRepository userRepository;
    private final NotificationQueueMapper queueMapper;
    private final NotificationQueueProperties queueProperties;

    @Transactional
    public NotificationQueueResponse enqueue(NotificationQueueRequest request) {
        ensureQueueEnabled();
        UserEntity user = userRepository
                .findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        enforceUserQuota(user.getId());
        int maxAttempts = Math.max(queueProperties.getMaxAttempts(), 1);

        NotificationQueue queue = NotificationQueue.builder()
                .user(user)
                .notificationType(request.getNotificationType())
                .title(request.getTitle())
                .message(request.getMessage())
                .data(request.getData())
                .actionUrl(request.getActionUrl())
                .priority(request.getPriority() != null ? request.getPriority() : DEFAULT_PRIORITY)
                .channel(request.getChannel())
                .scheduledFor(request.getScheduledFor() != null ? request.getScheduledFor() : Instant.now())
                .maxAttempts(maxAttempts)
                .build();

        queue = queueRepository.save(queue);
        return queueMapper.toResponse(queue);
    }

    @Transactional(readOnly = true)
    public long countActiveForUser(UUID userId) {
        if (!queueProperties.isEnabled()) {
            return 0;
        }
        return queueRepository.countByUserIdAndStatusIn(userId, ACTIVE_STATUSES);
    }

    @Transactional(readOnly = true)
    public List<NotificationQueueResponse> getQueueForUser(UUID userId, QueueStatus status) {
        List<NotificationQueue> queues = status == null
                ? queueRepository.findByUserIdOrderByScheduledForDesc(userId)
                : queueRepository.findByUserIdAndStatusOrderByScheduledForDesc(userId, status);
        return queues.stream().map(queueMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationQueueResponse> getDueNotifications(NotificationChannel channel) {
        if (!queueProperties.isEnabled()) {
            return List.of();
        }

        Instant now = Instant.now();
        Pageable pageable = PageRequest.of(
                0, queueProperties.getBatchSize(), Sort.by("scheduledFor").ascending());
        List<NotificationQueue> queues = channel == null
                ? queueRepository.findDueNotifications(QueueStatus.PENDING, now, pageable)
                : queueRepository.findDueNotificationsByChannel(QueueStatus.PENDING, now, channel, pageable);
        return queues.stream().map(queueMapper::toResponse).toList();
    }

    @Transactional
    public List<NotificationQueueResponse> processDueNotifications() {
        if (!queueProperties.isEnabled()) {
            log.trace("Notification queue disabled; skipping processing");
            return List.of();
        }

        Instant now = Instant.now();
        Pageable pageable = PageRequest.of(
                0, queueProperties.getBatchSize(), Sort.by("scheduledFor").ascending());
        List<NotificationQueue> queuedNotifications =
                queueRepository.findDueNotifications(QueueStatus.PENDING, now, pageable);

        if (queuedNotifications.isEmpty()) {
            return List.of();
        }

        for (NotificationQueue queue : queuedNotifications) {
            int attempts = queue.getAttempts() == null ? 0 : queue.getAttempts();
            queue.setAttempts(attempts + 1);
            queue.setStatus(QueueStatus.SENT);
            queue.setProcessedAt(now);
            queue.setLastAttemptAt(now);
        }

        queueRepository.saveAll(queuedNotifications);
        log.info("Processed {} queued notifications", queuedNotifications.size());
        return queuedNotifications.stream().map(queueMapper::toResponse).toList();
    }

    @Transactional
    public int retryFailedNotifications() {
        if (!queueProperties.isEnabled()) {
            return 0;
        }

        Instant retryAfter = Instant.now().minus(queueProperties.getRetryIntervalMinutes(), ChronoUnit.MINUTES);
        Pageable pageable = PageRequest.of(
                0, queueProperties.getBatchSize(), Sort.by("lastAttemptAt").ascending());

        List<NotificationQueue> candidates =
                queueRepository.findByStatusAndLastAttemptAtBefore(QueueStatus.FAILED, retryAfter, pageable);

        List<NotificationQueue> toRequeue = candidates.stream()
                .filter(this::isEligibleForRetry)
                .peek(this::resetForRetry)
                .toList();

        queueRepository.saveAll(candidates);
        log.info("Requeued {} failed notifications for retry", toRequeue.size());
        return toRequeue.size();
    }

    private boolean isEligibleForRetry(NotificationQueue queue) {
        int attempts = Optional.ofNullable(queue.getAttempts()).orElse(0);
        int maxAttempts = Optional.ofNullable(queue.getMaxAttempts()).orElse(queueProperties.getMaxAttempts());
        return attempts < maxAttempts;
    }

    private void resetForRetry(NotificationQueue queue) {
        queue.setStatus(QueueStatus.PENDING);
        queue.setScheduledFor(Instant.now());
        queue.resetLastAttemptAt();
        queue.resetErrorMessage();
    }

    private void ensureQueueEnabled() {
        if (!queueProperties.isEnabled()) {
            throw new NotificationQueueDisabledException("Notification queue is disabled");
        }
    }

    private void enforceUserQuota(UUID userId) {
        int maxPerUser = queueProperties.getMaxPerUser();
        if (maxPerUser <= 0) {
            return;
        }
        long activeCount = queueRepository.countByUserIdAndStatusIn(userId, ACTIVE_STATUSES);
        if (activeCount >= maxPerUser) {
            throw new NotificationQueueLimitException(
                    String.format("User %s has reached the notification queue limit (%d)", userId, maxPerUser));
        }
    }
}
