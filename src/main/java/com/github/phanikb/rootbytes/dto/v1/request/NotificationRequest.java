/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.notification.NotificationMetadataEntityType;
import com.github.phanikb.rootbytes.enums.notification.NotificationType;

import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.DATA_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.MESSAGE_REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.MESSAGE_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.TITLE_REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.TITLE_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.TYPE_REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.URL_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.USER_REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_M;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_XL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    @NotNull(message = USER_REQUIRED)
    private UserEntity user;

    @NotNull(message = TYPE_REQUIRED)
    private NotificationType type;

    @NotBlank(message = TITLE_REQUIRED)
    @Size(max = SIZE_M, message = TITLE_TOO_LONG)
    private String title;

    @NotBlank(message = MESSAGE_REQUIRED)
    @Size(max = SIZE_XL, message = MESSAGE_TOO_LONG)
    private String message;

    @Size(max = SIZE_XL, message = DATA_TOO_LONG)
    private String data;

    private NotificationMetadataEntityType entityType;
    private UUID entityId;

    @Size(max = SIZE_M, message = URL_TOO_LONG)
    private String actionUrl;
}
