/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.enums.notification.NotificationChannel;
import com.github.phanikb.rootbytes.enums.notification.NotificationPriority;
import com.github.phanikb.rootbytes.enums.notification.NotificationType;
import com.github.phanikb.rootbytes.enums.notification.QueueStatus;

@Entity
@Table(name = "notification_queue")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class NotificationQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String data;

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private NotificationPriority priority = NotificationPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(name = "scheduled_for", nullable = false)
    @Builder.Default
    private Instant scheduledFor = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private QueueStatus status = QueueStatus.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private Integer attempts = 0;

    @Column(name = "max_attempts", nullable = false)
    @Builder.Default
    private Integer maxAttempts = 3;

    @Column(name = "last_attempt_at")
    @Nullable
    private Instant lastAttemptAt;

    @Lob
    @Column(name = "error_message", columnDefinition = "TEXT")
    @Nullable
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "processed_at")
    @Nullable
    private Instant processedAt;

    @SuppressWarnings("PMD.NullAssignment")
    public void resetLastAttemptAt() {
        this.lastAttemptAt = null;
    }

    @SuppressWarnings("PMD.NullAssignment")
    public void resetErrorMessage() {
        this.errorMessage = null;
    }
}
