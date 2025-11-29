/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.mapper;

import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Set;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import com.github.phanikb.rootbytes.dto.v1.response.NotificationPreferenceResponse;
import com.github.phanikb.rootbytes.entity.NotificationPreference;
import com.github.phanikb.rootbytes.enums.notification.NotificationType;

@Component
public class NotificationPreferenceMapper {

    public NotificationPreferenceResponse toResponse(NotificationPreference preference) {
        Set<NotificationType> events = preference.getSubscribedEvents() == null
                ? EnumSet.noneOf(NotificationType.class)
                : preference.getSubscribedEvents();

        String[] eventNames = events.isEmpty()
                ? new String[0]
                : events.stream().map(NotificationType::name).toArray(String[]::new);

        return NotificationPreferenceResponse.builder()
                .userId(preference.getUserId())
                .emailEnabled(preference.getEmailEnabled())
                .smsEnabled(preference.getSmsEnabled())
                .frequency(preference.getFrequency())
                .quietHoursStart(optionalTime(preference.getQuietHoursStart()))
                .quietHoursEnd(optionalTime(preference.getQuietHoursEnd()))
                .subscribedEvents(eventNames)
                .build();
    }

    @Nullable
    private String optionalTime(@Nullable LocalTime time) {
        return time == null ? null : time.toString();
    }
}
