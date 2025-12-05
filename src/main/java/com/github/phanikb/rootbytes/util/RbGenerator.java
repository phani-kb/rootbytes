/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.util;

import java.security.SecureRandom;
import java.util.function.Predicate;

import org.jspecify.annotations.Nullable;

public final class RbGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int MAX_GENERATION_ATTEMPTS = 100;
    public static final int DEFAULT_LENGTH = 6;
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String ALPHANUMERIC_LOWERCASE = LOWERCASE_LETTERS + DIGITS;
    private static final String ALPHANUMERIC_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + DIGITS;
    private static final int MIN_SUFFIX_LENGTH = 1;

    private RbGenerator() {}

    public static String generateNumericOtp() {
        return generateNumericOtp(DEFAULT_LENGTH);
    }

    public static String generateNumericOtp(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        return generateRandom(DIGITS, length);
    }

    public static String generateAlphanumericOtp(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        return generateRandom(ALPHANUMERIC_UPPERCASE, length);
    }

    public static String generatePublicName(Predicate<String> isAvailable) {
        return generateWithLetterStart(ALPHANUMERIC_LOWERCASE, DEFAULT_LENGTH, isAvailable);
    }

    public static String generatePublicNameFromEmail(@Nullable String email, Predicate<String> isAvailable) {
        String baseName = extractBaseName(email);

        if (isAvailable.test(baseName)) {
            return baseName;
        }

        int maxSuffixLength = DEFAULT_LENGTH - baseName.length();
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            String suffix = generateNumericSuffix(maxSuffixLength);
            String candidate = baseName + suffix;

            if (isAvailable.test(candidate)) {
                return candidate;
            }
        }

        return generatePublicName(isAvailable);
    }

    public static String generateUsername(Predicate<String> isAvailable) {
        return generateWithLetterStart(ALPHANUMERIC_LOWERCASE, DEFAULT_LENGTH, isAvailable);
    }

    private static String generateWithLetterStart(String charset, int length, Predicate<String> isAvailable) {
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            StringBuilder result = new StringBuilder(length);

            result.append(LOWERCASE_LETTERS.charAt(SECURE_RANDOM.nextInt(LOWERCASE_LETTERS.length())));

            for (int i = 1; i < length; i++) {
                result.append(charset.charAt(SECURE_RANDOM.nextInt(charset.length())));
            }

            String candidate = result.toString();
            if (isAvailable.test(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException(
                "Failed to generate unique name after " + MAX_GENERATION_ATTEMPTS + " attempts");
    }

    private static String generateRandom(String charset, int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(charset.charAt(SECURE_RANDOM.nextInt(charset.length())));
        }
        return result.toString();
    }

    private static String extractBaseName(@Nullable String email) {
        if (email == null || email.isEmpty()) {
            return "user";
        }

        String username;
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            username = email;
        } else {
            username = email.substring(0, atIndex);
        }
        String cleaned = username.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(RbStringUtil.ROOT_LOCALE);

        if (cleaned.isEmpty()) {
            return "user";
        }

        if (!Character.isLetter(cleaned.charAt(0))) {
            cleaned = "u" + cleaned;
        }

        int maxBaseLength = DEFAULT_LENGTH - 2;
        if (cleaned.length() > maxBaseLength) {
            cleaned = cleaned.substring(0, maxBaseLength);
        }

        return cleaned;
    }

    private static String generateNumericSuffix(int maxLength) {
        if (maxLength <= 0) {
            return "";
        }
        if (maxLength == MIN_SUFFIX_LENGTH) {
            return String.valueOf(MIN_SUFFIX_LENGTH + SECURE_RANDOM.nextInt(9));
        }
        int upperBound = (int) Math.pow(10, maxLength) - 1;
        int lowerBound = (int) Math.pow(10, maxLength - 1);
        return String.valueOf(lowerBound + SECURE_RANDOM.nextInt(upperBound - lowerBound + 1));
    }
}
