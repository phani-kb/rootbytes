/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.enums.notification.NotificationFrequency;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceResponse {
    private UUID userId;
    private Boolean emailEnabled;
    private Boolean smsEnabled;
    private NotificationFrequency frequency;
    private String quietHoursStart;
    private String quietHoursEnd;
    private String[] subscribedEvents;
}
