/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.converter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Nullable;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.enums.notification.NotificationType;

@Slf4j
@Converter
public class NotificationTypeSetConverter implements AttributeConverter<Set<NotificationType>, String> {

    private static final String DELIMITER = ",";

    @Override
    @Nullable
    public String convertToDatabaseColumn(Set<NotificationType> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        EnumSet<NotificationType> subscribableOnly = attribute.stream()
                .filter(NotificationType::isSubscribable)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(NotificationType.class)));

        if (subscribableOnly.isEmpty()) {
            return null;
        }

        return subscribableOnly.stream().map(NotificationType::name).sorted().collect(Collectors.joining(DELIMITER));
    }

    @Override
    public Set<NotificationType> convertToEntityAttribute(String dbData) {
        EnumSet<NotificationType> events = EnumSet.noneOf(NotificationType.class);
        if (dbData == null || dbData.isBlank()) {
            return events;
        }

        Arrays.stream(dbData.split(DELIMITER))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .forEach(token -> {
                    NotificationType type = NotificationType.fromString(token);
                    if (type.isSubscribable()) {
                        events.add(type);
                    }
                });
        return events;
    }
}
