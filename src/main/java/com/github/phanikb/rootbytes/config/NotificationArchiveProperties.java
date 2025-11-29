/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "rootbytes.notification.archive")
@Validated
public class NotificationArchiveProperties {

    @Min(7)
    @Max(60)
    private int readAfterDays = 30;

    @Min(7)
    @Max(90)
    private int deleteAfterDays = 90;
}
