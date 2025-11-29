/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.notification.NotificationMetadataEntityType;
import com.github.phanikb.rootbytes.enums.notification.NotificationType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private UserEntity user;
    private NotificationType type;
    private String title;
    private String message;
    private String data;
    private NotificationMetadataEntityType entityType;
    private UUID entityId;
    private String actionUrl;
}
