/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublicNameValidatorTest {

    @Test
    void shouldAcceptValidPublicName() {
        var result = PublicNameValidator.validate("Kumar1");
        assertTrue(result.valid());
        assertEquals("Valid", result.message());
    }

    @Test
    void shouldAcceptSingleLetter() {
        var result = PublicNameValidator.validate("A");
        assertTrue(result.valid());
    }

    @Test
    void shouldAcceptMaxLengthName() {
        var result = PublicNameValidator.validate("Abcd12");
        assertTrue(result.valid());
    }

    @Test
    void shouldRejectNullPublicName() {
        var result = PublicNameValidator.validate(null);
        assertFalse(result.valid());
        assertEquals("Public name cannot be empty", result.message());
    }

    @Test
    void shouldRejectBlankPublicName() {
        var result = PublicNameValidator.validate("   ");
        assertFalse(result.valid());
        assertEquals("Public name cannot be empty", result.message());
    }

    @Test
    void shouldRejectEmptyPublicName() {
        var result = PublicNameValidator.validate("");
        assertFalse(result.valid());
        assertEquals("Public name cannot be empty", result.message());
    }

    @Test
    void shouldRejectTooLongPublicName() {
        var result = PublicNameValidator.validate("TooLong1");
        assertFalse(result.valid());
        assertEquals("Public name must not exceed 6 characters", result.message());
    }

    @Test
    void shouldRejectNameStartingWithNumber() {
        var result = PublicNameValidator.validate("1Kumar");
        assertFalse(result.valid());
        assertEquals("Public name must start with a letter", result.message());
    }

    @Test
    void shouldRejectNameWithSpecialCharacters() {
        var result = PublicNameValidator.validate("Jo@hn");
        assertFalse(result.valid());
        assertEquals("Public name can only contain letters and numbers", result.message());
    }

    @Test
    void shouldRejectNameWithUnderscore() {
        var result = PublicNameValidator.validate("Jo_hn");
        assertFalse(result.valid());
        assertEquals("Public name can only contain letters and numbers", result.message());
    }

    @Test
    void shouldRejectNameWithSpace() {
        var result = PublicNameValidator.validate("Jo hn");
        assertFalse(result.valid());
        assertEquals("Public name can only contain letters and numbers", result.message());
    }
}
