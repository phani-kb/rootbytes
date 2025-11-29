/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.github.phanikb.rootbytes.config.NotificationDigestProperties;
import com.github.phanikb.rootbytes.entity.NotificationPreference;
import com.github.phanikb.rootbytes.enums.notification.NotificationChannel;
import com.github.phanikb.rootbytes.enums.notification.NotificationFrequency;

@Service
@RequiredArgsConstructor
public class NotificationSchedulingService {

    private final NotificationDigestProperties notificationDigestProperties;

    public NotificationChannel determineChannel(NotificationPreference pref) {
        if (pref == null) {
            return NotificationChannel.IN_APP;
        }
        if (Boolean.TRUE.equals(pref.getEmailEnabled())) {
            return NotificationChannel.EMAIL;
        }
        if (Boolean.TRUE.equals(pref.getSmsEnabled())) {
            return NotificationChannel.SMS;
        }
        return NotificationChannel.IN_APP;
    }

    public Instant calculateScheduledTime(NotificationFrequency frequency) {
        if (frequency == null || frequency == NotificationFrequency.INSTANT) {
            return Instant.now();
        }

        if (!notificationDigestProperties.isEnabled()) {
            return Instant.now();
        }

        return calculateDigestTime(frequency);
    }

    private Instant calculateDigestTime(NotificationFrequency frequency) {
        Instant nowInstant = Instant.now();
        ZonedDateTime now = ZonedDateTime.ofInstant(nowInstant, ZoneOffset.UTC);

        switch (frequency) {
            case DAILY_DIGEST -> {
                ZonedDateTime candidate =
                        now.withHour(resolveDailyDigestHour()).withMinute(0).withSecond(0);

                if (!candidate.isAfter(now)) {
                    candidate = candidate.plusDays(1);
                }

                return candidate.toInstant();
            }
            case WEEKLY_DIGEST -> {
                DayOfWeek digestDay = resolveWeeklyDigestDay();

                ZonedDateTime candidate = now.with(TemporalAdjusters.nextOrSame(digestDay))
                        .withHour(resolveWeeklyDigestHour())
                        .withMinute(0);

                if (!candidate.isAfter(now)) {
                    candidate = candidate.plusWeeks(1);
                }

                return candidate.toInstant();
            }
            case MONTHLY_DIGEST -> {
                int digestDay = notificationDigestProperties.getMonthlyDay();

                ZonedDateTime candidate = now.with(TemporalAdjusters.firstDayOfNextMonth())
                        .withDayOfMonth(digestDay)
                        .withHour(notificationDigestProperties.getMonthlyHour())
                        .withMinute(0);

                if (!candidate.isAfter(now)) {
                    candidate = candidate.plusMonths(1);
                }

                return candidate.toInstant();
            }
            case INSTANT -> {
                return nowInstant;
            }
        }

        throw new IllegalStateException("Unhandled notification frequency: " + frequency);
    }

    private int resolveDailyDigestHour() {
        return sanitizeHour(
                notificationDigestProperties.getDailyHour(), NotificationDigestProperties.DigestDefaults.HOUR);
    }

    private DayOfWeek resolveWeeklyDigestDay() {
        if (!notificationDigestProperties.isEnabled() || notificationDigestProperties.getWeeklyDay() == null) {
            return NotificationDigestProperties.DigestDefaults.WEEKLY_DAY;
        }
        return notificationDigestProperties.getWeeklyDay();
    }

    private int resolveWeeklyDigestHour() {
        int configured = notificationDigestProperties.isEnabled()
                ? notificationDigestProperties.getWeeklyHour()
                : NotificationDigestProperties.DigestDefaults.HOUR;
        return sanitizeHour(configured, NotificationDigestProperties.DigestDefaults.HOUR);
    }

    private int sanitizeHour(int hour, int fallback) {
        if (hour < 0 || hour > 23) {
            return fallback;
        }
        return hour;
    }
}
