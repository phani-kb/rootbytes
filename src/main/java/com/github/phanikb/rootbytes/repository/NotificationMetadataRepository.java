/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.NotificationMetadata;

@Repository
public interface NotificationMetadataRepository extends JpaRepository<NotificationMetadata, UUID> {

    Optional<NotificationMetadata> findByNotificationId(UUID notificationId);

    List<NotificationMetadata> findByEntityTypeAndEntityId(String entityType, UUID entityId);
}
