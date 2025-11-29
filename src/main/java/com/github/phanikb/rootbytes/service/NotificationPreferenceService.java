/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.dto.v1.request.NotificationPreferenceRequest;
import com.github.phanikb.rootbytes.dto.v1.response.NotificationPreferenceResponse;
import com.github.phanikb.rootbytes.entity.NotificationPreference;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.notification.NotificationFrequency;
import com.github.phanikb.rootbytes.enums.notification.NotificationType;
import com.github.phanikb.rootbytes.exception.ResourceNotFoundException;
import com.github.phanikb.rootbytes.mapper.NotificationPreferenceMapper;
import com.github.phanikb.rootbytes.repository.NotificationPreferenceRepository;
import com.github.phanikb.rootbytes.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final NotificationPreferenceMapper preferenceMapper;

    @Transactional(readOnly = true)
    public NotificationPreferenceResponse getPreferences(UUID userId) {
        NotificationPreference preference =
                preferenceRepository.findByUserId(userId).orElse(buildDefault(userId));
        return preferenceMapper.toResponse(preference);
    }

    @Transactional
    public NotificationPreferenceResponse updatePreferences(UUID userId, NotificationPreferenceRequest request) {
        NotificationPreference preference =
                preferenceRepository.findByUserId(userId).orElseGet(() -> createPreference(userId));

        mergePreference(preference, request);
        preference = preferenceRepository.save(preference);

        return preferenceMapper.toResponse(preference);
    }

    private NotificationPreference buildDefault(UUID userId) {
        return NotificationPreference.builder()
                .userId(userId)
                .emailEnabled(false)
                .smsEnabled(false)
                .frequency(NotificationFrequency.INSTANT)
                .subscribedEvents(EnumSet.noneOf(NotificationType.class))
                .build();
    }

    private NotificationPreference createPreference(UUID userId) {
        UserEntity user =
                userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return NotificationPreference.builder()
                .userId(userId)
                .user(user)
                .emailEnabled(false)
                .smsEnabled(false)
                .frequency(NotificationFrequency.INSTANT)
                .subscribedEvents(EnumSet.noneOf(NotificationType.class))
                .build();
    }

    @Transactional
    public NotificationPreference getOrCreatePreference(UserEntity user) {
        return preferenceRepository.findByUserId(user.getId()).orElseGet(() -> {
            NotificationPreference pref = NotificationPreference.builder()
                    .userId(user.getId())
                    .user(user)
                    .emailEnabled(false)
                    .smsEnabled(false)
                    .frequency(NotificationFrequency.INSTANT)
                    .build();
            return preferenceRepository.save(pref);
        });
    }

    public boolean shouldNotify(@Nullable NotificationPreference pref, NotificationType type) {
        if (!type.isSubscribable()) {
            return true;
        }
        if (pref == null) {
            return true;
        }
        Set<NotificationType> subscribedEvents = pref.getSubscribedEvents();
        return subscribedEvents == null || subscribedEvents.isEmpty() || subscribedEvents.contains(type);
    }

    private void mergePreference(NotificationPreference preference, NotificationPreferenceRequest request) {
        if (request.getEmailEnabled() != null) {
            preference.setEmailEnabled(request.getEmailEnabled());
        }
        if (request.getSmsEnabled() != null) {
            preference.setSmsEnabled(request.getSmsEnabled());
        }
        if (request.getFrequency() != null) {
            preference.setFrequency(request.getFrequency());
        }
        if (StringUtils.hasText(request.getQuietHoursStart())) {
            preference.setQuietHoursStart(parseTime(request.getQuietHoursStart()));
        } else if (request.getQuietHoursStart() != null) {
            preference.setQuietHoursStart(null);
        }
        if (StringUtils.hasText(request.getQuietHoursEnd())) {
            preference.setQuietHoursEnd(parseTime(request.getQuietHoursEnd()));
        } else if (request.getQuietHoursEnd() != null) {
            preference.setQuietHoursEnd(null);
        }
        if (request.getSubscribedEvents() != null) {
            preference.setSubscribedEvents(NotificationType.toNotificationTypeSet(request.getSubscribedEvents()));
        }
    }

    private LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(value);
        } catch (DateTimeException ex) {
            log.warn("Failed to parse time: {}", value, ex);
            throw new IllegalArgumentException("Invalid time format: " + value, ex);
        }
    }
}
