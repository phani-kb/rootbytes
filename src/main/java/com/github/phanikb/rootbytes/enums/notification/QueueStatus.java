/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.enums.notification;

import java.util.EnumSet;
import java.util.Set;

public enum QueueStatus {
    PENDING,
    PROCESSING,
    SENT,
    FAILED,
    CANCELLED;

    public static Set<QueueStatus> retryableStatuses() {
        return EnumSet.of(FAILED);
    }

    public static Set<QueueStatus> activeQueueStatuses() {
        return EnumSet.of(PENDING, PROCESSING);
    }
}
