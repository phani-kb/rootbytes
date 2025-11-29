/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.entity.Notification;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.notification.NotificationStatus;
import com.github.phanikb.rootbytes.repository.NotificationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification createNotification(UserEntity user, String type, String message) {
        log.info("Creating notification for user: {}", user.getEmail());

        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .status(NotificationStatus.UNREAD)
                .build();

        return notificationRepository.save(notification);
    }
}
