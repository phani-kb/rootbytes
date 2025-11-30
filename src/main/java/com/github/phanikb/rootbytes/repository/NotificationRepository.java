/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.Notification;
import com.github.phanikb.rootbytes.enums.notification.NotificationStatus;
import com.github.phanikb.rootbytes.enums.notification.NotificationType;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUserIdAndStatus(UUID userId, NotificationStatus status, Pageable pageable);

    Page<Notification> findByUserIdAndStatusNot(UUID userId, NotificationStatus status, Pageable pageable);

    Optional<Notification> findByIdAndUserId(UUID id, UUID userId);

    long countByUserIdAndStatus(UUID userId, NotificationStatus status);

    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE Notification n SET n.status = 'READ', n.readAt = :readAt WHERE n.user.id = :userId AND n.status = 'UNREAD'")
    int markAllAsReadForUser(@Param("userId") UUID userId, @Param("readAt") Instant readAt);

    List<Notification> findByUserIdAndStatusAndTypeIn(
            UUID userId, NotificationStatus status, List<NotificationType> types);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.status = 'ARCHIVED', n.archivedAt = :archivedAt "
            + "WHERE n.status = 'READ' AND n.readAt <= :readCutoff")
    int archiveReadNotifications(@Param("readCutoff") Instant readCutoff, @Param("archivedAt") Instant archivedAt);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Notification n WHERE n.status = 'ARCHIVED' AND n.archivedAt <= :archiveCutoff")
    int deleteArchivedNotifications(@Param("archiveCutoff") Instant archiveCutoff);
}
