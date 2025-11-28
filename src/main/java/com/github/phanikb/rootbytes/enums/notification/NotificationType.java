/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.enums.notification;

public enum NotificationType {
    REGISTRATION_CONFIRMATION(true, true, NotificationPriority.HIGH),
    EMAIL_VERIFICATION(true, true, NotificationPriority.HIGH),
    PASSWORD_RESET(true, true, NotificationPriority.HIGH),
    ACCOUNT_ACTIVATED(true, false, NotificationPriority.MEDIUM),
    SECURITY_ALERT(true, true, NotificationPriority.CRITICAL),
    GENERAL(false, false, NotificationPriority.LOW, true, true);

    private final boolean external;
    private final boolean requiresAction;
    private final NotificationPriority priority;
    private final boolean digestible;
    private final boolean subscribable;

    NotificationType(NotificationPriority priority) {
        this(false, false, priority, false, false);
    }

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

    public boolean isExternal() {
        return external;
    }

    public boolean requiresAction() {
        return requiresAction;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public boolean isDigestible() {
        return digestible;
    }

    public boolean isSubscribable() {
        return subscribable;
    }

    public static NotificationType fromString(String typeStr) {
        if (typeStr == null || typeStr.isBlank()) {
            return NotificationType.GENERAL;
        }
        for (NotificationType type : NotificationType.values()) {
            if (type.name().equalsIgnoreCase(typeStr)) {
                return type;
            }
        }
        return NotificationType.GENERAL;
    }
}
