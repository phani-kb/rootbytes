/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UpperCaseConverterTest {

    private UpperCaseConverter converter;

    @BeforeEach
    void setUp() {
        converter = new UpperCaseConverter();
    }

    @Test
    void shouldConvertToUpperCaseForDatabase() {
        assertEquals("HELLO", converter.convertToDatabaseColumn("hello"));
        assertEquals("HELLO WORLD", converter.convertToDatabaseColumn("Hello World"));
        assertEquals("TEST", converter.convertToDatabaseColumn("  test  "));
    }

    @Test
    void shouldReturnEmptyStringForNullInput() {
        assertEquals("", converter.convertToDatabaseColumn(null));
    }

    @Test
    void shouldReturnSameValueFromDatabase() {
        assertEquals("HELLO", converter.convertToEntityAttribute("HELLO"));
        assertEquals("hello", converter.convertToEntityAttribute("hello"));
        assertNull(converter.convertToEntityAttribute(null));
    }

    @Test
    void shouldHandleEmptyString() {
        assertEquals("", converter.convertToDatabaseColumn(""));
        assertEquals("", converter.convertToDatabaseColumn("   "));
    }

    @Test
    void shouldHandleSpecialCharacters() {
        assertEquals("TEST-NAME", converter.convertToDatabaseColumn("test-name"));
        assertEquals("O'CONNOR", converter.convertToDatabaseColumn("O'Connor"));
    }
}
