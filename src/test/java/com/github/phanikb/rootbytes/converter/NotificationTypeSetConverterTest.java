/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.converter;

import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.phanikb.rootbytes.enums.notification.NotificationType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationTypeSetConverterTest {

    private NotificationTypeSetConverter converter;

    @BeforeEach
    void setUp() {
        converter = new NotificationTypeSetConverter();
    }

    @Test
    void shouldConvertSetToDatabaseColumn() {
        Set<NotificationType> types = EnumSet.of(NotificationType.GENERAL);

        String result = converter.convertToDatabaseColumn(types);

        assertNotNull(result);
        assertTrue(result.contains("GENERAL"));
    }

    @Test
    void shouldReturnNullForEmptySet() {
        Set<NotificationType> types = EnumSet.noneOf(NotificationType.class);
        assertNull(converter.convertToDatabaseColumn(types));
    }

    @Test
    void shouldReturnNullForNullSet() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void shouldConvertDatabaseColumnToSet() {
        String dbData = "GENERAL";

        Set<NotificationType> result = converter.convertToEntityAttribute(dbData);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(NotificationType.GENERAL));
    }

    @Test
    void shouldReturnEmptySetForNullDbData() {
        Set<NotificationType> result = converter.convertToEntityAttribute(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptySetForBlankDbData() {
        Set<NotificationType> result = converter.convertToEntityAttribute("   ");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldHandleWhitespaceInDbData() {
        String dbData = " GENERAL ";

        Set<NotificationType> result = converter.convertToEntityAttribute(dbData);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void shouldFilterNonSubscribableTypes() {
        Set<NotificationType> types = EnumSet.of(
                NotificationType.GENERAL, // subscribable
                NotificationType.PASSWORD_RESET); // not subscribable

        String result = converter.convertToDatabaseColumn(types);

        assertNotNull(result);
        assertTrue(result.contains("GENERAL"));
    }

    @Test
    void shouldReturnNullWhenAllTypesAreNonSubscribable() {
        Set<NotificationType> types = EnumSet.of(NotificationType.PASSWORD_RESET, NotificationType.SECURITY_ALERT);

        String result = converter.convertToDatabaseColumn(types);

        assertNull(result);
    }
}
