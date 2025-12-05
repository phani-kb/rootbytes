/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RbGeneratorTest {

    @Test
    void shouldGenerateNumericOtpWithDefaultLength() {
        String otp = RbGenerator.generateNumericOtp();
        assertNotNull(otp);
        assertEquals(RbGenerator.DEFAULT_LENGTH, otp.length());
        assertTrue(otp.matches("\\d+"));
    }

    @Test
    void shouldGenerateNumericOtpWithCustomLength() {
        String otp = RbGenerator.generateNumericOtp(8);
        assertNotNull(otp);
        assertEquals(8, otp.length());
        assertTrue(otp.matches("\\d+"));
    }

    @Test
    void shouldThrowExceptionForInvalidOtpLength() {
        assertThrows(IllegalArgumentException.class, () -> RbGenerator.generateNumericOtp(0));
        assertThrows(IllegalArgumentException.class, () -> RbGenerator.generateNumericOtp(-1));
    }

    @Test
    void shouldGenerateAlphanumericOtp() {
        String otp = RbGenerator.generateAlphanumericOtp(8);
        assertNotNull(otp);
        assertEquals(8, otp.length());
        assertTrue(otp.matches("[A-Z0-9]+"));
    }

    @Test
    void shouldThrowExceptionForInvalidAlphanumericOtpLength() {
        assertThrows(IllegalArgumentException.class, () -> RbGenerator.generateAlphanumericOtp(0));
        assertThrows(IllegalArgumentException.class, () -> RbGenerator.generateAlphanumericOtp(-1));
    }

    @Test
    void shouldGeneratePublicNameStartingWithLetter() {
        String name = RbGenerator.generatePublicName(s -> true);
        assertNotNull(name);
        assertEquals(RbGenerator.DEFAULT_LENGTH, name.length());
        assertTrue(Character.isLetter(name.charAt(0)));
        assertTrue(name.matches("[a-z][a-z0-9]{5}"));
    }

    @Test
    void shouldGenerateUniquePublicNameWhenFirstNotAvailable() {
        int[] counter = {0};
        String name = RbGenerator.generatePublicName(s -> {
            counter[0]++;
            return counter[0] > 3; // 3 attempts
        });
        assertNotNull(name);
        assertEquals(RbGenerator.DEFAULT_LENGTH, name.length());
    }

    @Test
    void shouldGenerateUsernameStartingWithLetter() {
        String name = RbGenerator.generateUsername(s -> true);
        assertNotNull(name);
        assertEquals(RbGenerator.DEFAULT_LENGTH, name.length());
        assertTrue(Character.isLetter(name.charAt(0)));
    }

    @Test
    void shouldGeneratePublicNameFromEmail() {
        String name = RbGenerator.generatePublicNameFromEmail("kumar@example.com", s -> true);
        assertNotNull(name);
        assertTrue(name.length() <= RbGenerator.DEFAULT_LENGTH);
        assertTrue(Character.isLetter(name.charAt(0)));
    }

    @Test
    void shouldGeneratePublicNameFromEmailWithFallback() {
        String name = RbGenerator.generatePublicNameFromEmail("kumar@example.com", s -> !s.equals("kumar"));
        assertNotNull(name);
        assertTrue(name.length() <= RbGenerator.DEFAULT_LENGTH);
    }

    @Test
    void shouldGeneratePublicNameFromNullEmail() {
        String name = RbGenerator.generatePublicNameFromEmail(null, s -> true);
        assertNotNull(name);
        assertTrue(!name.isEmpty() && name.length() <= RbGenerator.DEFAULT_LENGTH);
        assertTrue(Character.isLetter(name.charAt(0)));
    }

    @Test
    void shouldGenerateDifferentOtpsEachTime() {
        String otp1 = RbGenerator.generateNumericOtp();
        String otp2 = RbGenerator.generateNumericOtp();
        assertFalse(otp1.equals(otp2) && RbGenerator.generateNumericOtp().equals(otp1));
    }
}
