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
@ConfigurationProperties(prefix = "rootbytes.notification.queue")
@Validated
public class NotificationQueueProperties {

    private boolean enabled = true;

    @Min(2)
    @Max(50)
    private int batchSize = 20;

    @Min(5)
    @Max(60)
    private int retryIntervalMinutes = 5;

    @Min(1)
    @Max(10)
    private int maxAttempts = 3;

    @Min(1)
    @Max(100)
    private int maxPerUser = 50;

    @Min(5)
    @Max(60)
    private int processingIntervalMinutes = 15;

    public int getProcessingIntervalMinutes() {
        return processingIntervalMinutes > 0 ? processingIntervalMinutes : 15;
    }
}
