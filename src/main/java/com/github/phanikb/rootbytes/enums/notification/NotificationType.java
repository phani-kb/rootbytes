/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.enums.notification;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.util.RbStringUtil;

@Slf4j
@Getter
public enum NotificationType {
    REGISTRATION_CONFIRMATION(true, true, NotificationPriority.HIGH),
    EMAIL_VERIFICATION(true, true, NotificationPriority.HIGH),
    PASSWORD_RESET(true, true, NotificationPriority.HIGH),
    ACCOUNT_ACTIVATED(true, false, NotificationPriority.MEDIUM),
    SECURITY_ALERT(true, true, NotificationPriority.CRITICAL),
    GENERAL(false, false, NotificationPriority.LOW, true, true);

    @Getter
    private final boolean external;

    private final boolean requiresAction;

    @Getter
    private final NotificationPriority priority;

    private final boolean digestible;

    @Getter
    private final boolean subscribable;

    NotificationType(boolean external, boolean requiresAction, NotificationPriority priority) {
        this(external, requiresAction, priority, false, false);
    }

    NotificationType(
            boolean external,
            boolean requiresAction,
            NotificationPriority priority,
            boolean digestible,
            boolean subscribable) {
        this.external = external;
        this.requiresAction = requiresAction;
        this.priority = priority;
        this.digestible = digestible;
        this.subscribable = subscribable;
    }

    public boolean isDigestible() {
        return digestible;
    }

    public static NotificationType fromString(String typeStr) {
        if (typeStr == null || typeStr.isBlank()) {
            return GENERAL;
        }
        for (NotificationType type : values()) {
            if (RbStringUtil.equalsIgnoreCase(type.name(), typeStr)) {
                return type;
            }
        }
        log.warn("Failed to resolve notification type: {}, using default: {}", typeStr, GENERAL);
        return GENERAL;
    }

    public static Set<NotificationType> toNotificationTypeSet(String... values) {
        if (values.length == 0) {
            return EnumSet.noneOf(NotificationType.class);
        }

        return Arrays.stream(values)
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .map(NotificationType::fromString)
                .filter(NotificationType::isSubscribable)
                .collect(Collectors.toSet());
    }
}
