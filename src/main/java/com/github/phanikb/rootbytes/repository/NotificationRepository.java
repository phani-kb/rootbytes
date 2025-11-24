/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.phanikb.rootbytes.entity.Notification;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.NotificationStatus;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserAndStatus(UserEntity user, NotificationStatus status);

    List<Notification> findByUserOrderByCreatedAtDesc(UserEntity user);

    long countByUserAndStatus(UserEntity user, NotificationStatus status);
}
