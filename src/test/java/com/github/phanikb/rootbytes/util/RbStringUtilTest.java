/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RbStringUtilTest {

    @Test
    void shouldReturnTrueForNullString() {
        assertTrue(RbStringUtil.isNullOrEmpty(null));
    }

    @Test
    void shouldReturnTrueForEmptyString() {
        assertTrue(RbStringUtil.isNullOrEmpty(""));
    }

    @Test
    void shouldReturnFalseForNonEmptyString() {
        assertFalse(RbStringUtil.isNullOrEmpty("hello"));
        assertFalse(RbStringUtil.isNullOrEmpty(" "));
    }

    @Test
    void shouldCompareStringsIgnoringCase() {
        assertTrue(RbStringUtil.equalsIgnoreCase("hello", "HELLO"));
        assertTrue(RbStringUtil.equalsIgnoreCase("Hello", "hElLo"));
        assertFalse(RbStringUtil.equalsIgnoreCase("hello", "world"));
    }

    @Test
    void shouldHandleNullInEqualsIgnoreCase() {
        assertTrue(RbStringUtil.equalsIgnoreCase(null, null));
        assertFalse(RbStringUtil.equalsIgnoreCase("hello", null));
        assertFalse(RbStringUtil.equalsIgnoreCase(null, "hello"));
    }

    @Test
    void shouldCompareStrings() {
        assertEquals(0, RbStringUtil.compare("hello", "hello"));
        assertTrue(RbStringUtil.compare("apple", "banana") < 0);
        assertTrue(RbStringUtil.compare("banana", "apple") > 0);
    }

    @Test
    void shouldHandleNullInCompare() {
        assertEquals(0, RbStringUtil.compare(null, null));
        assertEquals(-1, RbStringUtil.compare(null, "hello"));
        assertEquals(1, RbStringUtil.compare("hello", null));
    }

    @Test
    void shouldConvertToLowerCase() {
        assertEquals("hello", RbStringUtil.toLowerCase("HELLO"));
        assertEquals("hello world", RbStringUtil.toLowerCase("Hello World"));
        assertNull(RbStringUtil.toLowerCase(null));
    }

    @Test
    void shouldConvertToUpperCase() {
        assertEquals("HELLO", RbStringUtil.toUpperCase("hello"));
        assertEquals("HELLO WORLD", RbStringUtil.toUpperCase("Hello World"));
        assertNull(RbStringUtil.toUpperCase(null));
    }
}
