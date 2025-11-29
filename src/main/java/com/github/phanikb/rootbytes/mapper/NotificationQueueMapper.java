/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.github.phanikb.rootbytes.dto.v1.response.NotificationQueueResponse;
import com.github.phanikb.rootbytes.entity.NotificationQueue;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN)
public class NotificationQueueMapper {

    public NotificationQueueResponse toResponse(NotificationQueue entity) {
        return NotificationQueueResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .notificationType(entity.getNotificationType())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .data(entity.getData())
                .actionUrl(entity.getActionUrl())
                .priority(entity.getPriority())
                .channel(entity.getChannel())
                .status(entity.getStatus())
                .scheduledFor(entity.getScheduledFor())
                .createdAt(entity.getCreatedAt())
                .processedAt(entity.getProcessedAt())
                .build();
    }
}
