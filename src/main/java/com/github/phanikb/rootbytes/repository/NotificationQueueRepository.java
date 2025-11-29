/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.NotificationQueue;
import com.github.phanikb.rootbytes.enums.notification.NotificationChannel;
import com.github.phanikb.rootbytes.enums.notification.QueueStatus;

@Repository
public interface NotificationQueueRepository extends JpaRepository<NotificationQueue, UUID> {

    long countByStatusAndChannel(QueueStatus status, NotificationChannel channel);

    @Query("SELECT nq FROM NotificationQueue nq WHERE nq.status = :status "
            + "AND nq.scheduledFor <= :cutoff ORDER BY nq.scheduledFor ASC")
    List<NotificationQueue> findDueNotifications(
            @Param("status") QueueStatus status, @Param("cutoff") Instant cutoff, Pageable pageable);

    @Query("SELECT nq FROM NotificationQueue nq WHERE nq.status = :status "
            + "AND nq.scheduledFor <= :cutoff AND nq.channel = :channel ORDER BY nq.scheduledFor ASC")
    List<NotificationQueue> findDueNotificationsByChannel(
            @Param("status") QueueStatus status,
            @Param("cutoff") Instant cutoff,
            @Param("channel") NotificationChannel channel,
            Pageable pageable);

    List<NotificationQueue> findByUserIdOrderByScheduledForDesc(UUID userId);

    List<NotificationQueue> findByUserIdAndStatusOrderByScheduledForDesc(UUID userId, QueueStatus status);

    List<NotificationQueue> findByStatusAndLastAttemptAtBefore(
            QueueStatus status, Instant retryAfter, Pageable pageable);

    long countByUserIdAndStatusIn(UUID userId, Collection<QueueStatus> statuses);
}
