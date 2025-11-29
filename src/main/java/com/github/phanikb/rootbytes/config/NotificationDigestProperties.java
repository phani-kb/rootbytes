/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.config;

import java.time.DayOfWeek;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "rootbytes.notification.digest")
@Validated
public class NotificationDigestProperties {

    private boolean enabled = false;

    @Min(1)
    @Max(23)
    private int dailyHour = 6;

    private DayOfWeek weeklyDay = DayOfWeek.MONDAY;

    @Min(0)
    @Max(23)
    private int weeklyHour = 6;

    @Min(1)
    @Max(28)
    private int monthlyDay = 1;

    @Min(0)
    @Max(23)
    private int monthlyHour = 6;
}
