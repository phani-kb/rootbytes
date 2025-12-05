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

class PasswordValidatorTest {

    @Test
    void shouldAcceptValidPassword() {
        var result = PasswordValidator.validate("Xk9#mLp2$Qr");
        assertTrue(result.valid(), "Password should be valid: " + result.message());
        assertEquals("Valid", result.message());
    }

    @Test
    void shouldRejectNullPassword() {
        var result = PasswordValidator.validate(null);
        assertFalse(result.valid());
        assertEquals("Password cannot be empty", result.message());
    }

    @Test
    void shouldRejectEmptyPassword() {
        var result = PasswordValidator.validate("");
        assertFalse(result.valid());
        assertEquals("Password cannot be empty", result.message());
    }

    @Test
    void shouldRejectTooShortPassword() {
        var result = PasswordValidator.validate("Sh1!aaa");
        assertFalse(result.valid());
        assertEquals("Password must be at least 8 characters", result.message());
    }

    @Test
    void shouldRejectTooLongPassword() {
        String longPassword = "A" + "a".repeat(124) + "1!" + "x".repeat(10);
        var result = PasswordValidator.validate(longPassword);
        assertFalse(result.valid());
        assertEquals("Password must not exceed 128 characters", result.message());
    }

    @Test
    void shouldRequireUppercaseLetter() {
        var result = PasswordValidator.validate("lowercase1!");
        assertFalse(result.valid());
        assertTrue(result.message().contains("uppercase letter"));
    }

    @Test
    void shouldRequireLowercaseLetter() {
        var result = PasswordValidator.validate("UPPERCASE1!");
        assertFalse(result.valid());
        assertTrue(result.message().contains("lowercase letter"));
    }

    @Test
    void shouldRequireDigit() {
        var result = PasswordValidator.validate("NoDigitHere!");
        assertFalse(result.valid());
        assertTrue(result.message().contains("digit"));
    }

    @Test
    void shouldRequireSpecialCharacter() {
        var result = PasswordValidator.validate("NoSpecial1a");
        assertFalse(result.valid());
        assertTrue(result.message().contains("special character"));
    }

    @Test
    void shouldRejectWeakPassword() {
        var result = PasswordValidator.validate("Password1!");
        assertFalse(result.valid());
        assertTrue(result.message().contains("too common") || result.message().contains("sequential"));
    }

    @Test
    void shouldRejectSequentialNumbers() {
        var result = PasswordValidator.validate("Test12345A!");
        assertFalse(result.valid());
    }

    @Test
    void shouldRejectSequentialLetters() {
        var result = PasswordValidator.validate("Testabcde1!");
        assertFalse(result.valid());
    }

    @Test
    void shouldIdentifyWeakPassword() {
        assertTrue(PasswordValidator.isWeakPassword(null));
        assertTrue(PasswordValidator.isWeakPassword("short"));
        assertTrue(PasswordValidator.isWeakPassword("password123"));
    }

    @Test
    void shouldIdentifyStrongPassword() {
        assertFalse(PasswordValidator.isWeakPassword("Xk9mLp2Qr8Zt"));
    }

    @Test
    void shouldAcceptPasswordWithVariousSpecialChars() {
        assertTrue(PasswordValidator.validate("Xk9#mLp2Qr").valid());
        assertTrue(PasswordValidator.validate("Ym8@nKq3Ws").valid());
        assertTrue(PasswordValidator.validate("Zn7$oJr4Xt").valid());
    }

    @Test
    void shouldAcceptPasswordAtMinLength() {
        // 8 chars minimum
        var result = PasswordValidator.validate("Xk9#mLp2");
        assertTrue(result.valid(), "Password should be valid: " + result.message());
    }

    @Test
    void shouldAcceptPasswordAtMaxLength() {
        // 128 chars
        StringBuilder sb = new StringBuilder();
        sb.append("Xk9#".repeat(31)); // 4 chars × 31 = 124
        sb.append("Lp2!"); // + 4 = 128 chars
        String maxPassword = sb.toString();
        assertEquals(128, maxPassword.length());
        var result = PasswordValidator.validate(maxPassword);
        assertTrue(result.valid(), "Password should be valid: " + result.message());
    }
}
