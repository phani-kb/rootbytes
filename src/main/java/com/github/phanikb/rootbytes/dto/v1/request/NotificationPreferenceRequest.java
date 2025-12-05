/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.enums.notification.NotificationFrequency;

import static com.github.phanikb.rootbytes.common.ValidationConstants.MAX_SUBSCRIBED_EVENTS;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceRequest {
    private Boolean emailEnabled;
    private Boolean smsEnabled;
    private NotificationFrequency frequency;

    @Pattern(regexp = "^([01]?\\d|2[0-3]):[0-5]\\d$", message = "Quiet hours start must be in HH:mm format")
    private String quietHoursStart;

    @Pattern(regexp = "^([01]?\\d|2[0-3]):[0-5]\\d$", message = "Quiet hours end must be in HH:mm format")
    private String quietHoursEnd;

    @Size(max = MAX_SUBSCRIBED_EVENTS, message = "Maximum " + MAX_SUBSCRIBED_EVENTS + " subscribed events allowed")
    private String[] subscribedEvents;
}
