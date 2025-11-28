/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.enums.notification;

public enum NotificationPriority {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW;

    public static NotificationPriority getDefault() {
        return MEDIUM;
    }
}
