/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogSanitizerTest {

    @Test
    void shouldSanitizeNullMessage() {
        assertEquals("null", LogSanitizer.sanitize((String) null));
    }

    @Test
    void shouldRemoveNewlineCharacters() {
        assertEquals("hello world", LogSanitizer.sanitize("hello\nworld"));
        assertEquals("hello world", LogSanitizer.sanitize("hello\r\nworld"));
        assertEquals("hello world", LogSanitizer.sanitize("hello\rworld"));
    }

    @Test
    void shouldRemoveTabCharacters() {
        assertEquals("hello world", LogSanitizer.sanitize("hello\tworld"));
    }

    @Test
    void shouldCollapseMultipleSpaces() {
        assertEquals("hello world", LogSanitizer.sanitize("hello    world"));
        assertEquals("hello world", LogSanitizer.sanitize("  hello   world  "));
    }

    @Test
    void shouldSanitizeThrowable() {
        Exception ex = new Exception("Error\nmessage\twith special chars");
        assertEquals("Error message with special chars", LogSanitizer.sanitize(ex));
    }

    @Test
    void shouldHandleNullThrowable() {
        assertEquals("No throwable", LogSanitizer.sanitize((Throwable) null));
    }

    @Test
    void shouldHandleThrowableWithNullMessage() {
        Exception ex = new Exception((String) null);
        assertEquals("null", LogSanitizer.sanitize(ex));
    }

    @Test
    void shouldSanitizeAndTruncate() {
        String longMessage = "This is a very long message that needs to be truncated";
        String result = LogSanitizer.sanitizeAndTruncate(longMessage, 20);
        assertEquals("This is a very lo...", result);
        assertEquals(20, result.length());
    }

    @Test
    void shouldNotTruncateShortMessage() {
        String shortMessage = "Short";
        String result = LogSanitizer.sanitizeAndTruncate(shortMessage, 20);
        assertEquals("Short", result);
    }

    @Test
    void shouldSanitizeBeforeTruncating() {
        String message = "Hello\n\n\nWorld";
        String result = LogSanitizer.sanitizeAndTruncate(message, 20);
        assertEquals("Hello World", result);
    }
}
