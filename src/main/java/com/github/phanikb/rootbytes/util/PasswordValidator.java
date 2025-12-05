/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.jspecify.annotations.Nullable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("\\d");
    private static final Pattern SPECIAL = Pattern.compile("[!@#$%^&*(),.?\":{}|<>_\\-+=\\[\\]\\\\;'/`~]");
    private static final Pattern SEQUENTIAL_NUMBERS = Pattern.compile("012|123|234|345|456|567|678|789|890");
    private static final String DEFAULT_WEAK_PASSWORDS_PATH = "/security/weak-passwords.txt";
    private static final String WEAK_PASSWORDS_PATH_PROPERTY = "rootbytes.security.weak-passwords-path";
    private static final Set<String> WEAK_PASSWORDS = loadWeakPasswords();

    private PasswordValidator() {}

    private static Set<String> loadWeakPasswords() {
        String resourcePath = System.getProperty(WEAK_PASSWORDS_PATH_PROPERTY, DEFAULT_WEAK_PASSWORDS_PATH);
        Set<String> passwords = new HashSet<>();

        try (InputStream is = PasswordValidator.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                log.warn("Weak passwords file not found: {}, using empty set", resourcePath);
                return Collections.emptySet();
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line = reader.readLine();
                while (line != null) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                        passwords.add(RbStringUtil.toLowerCase(trimmed));
                    }
                    line = reader.readLine();
                }
            }

            log.info("Loaded {} weak passwords from {}", passwords.size(), resourcePath);
            return Collections.unmodifiableSet(passwords);

        } catch (IOException e) {
            log.error("Failed to load weak passwords from {}", resourcePath, e);
            return Collections.emptySet();
        }
    }

    public static ValidationResult validate(@Nullable String password) {
        if (password == null || password.isEmpty()) {
            return ValidationResult.invalidResult("Password cannot be empty");
        }

        ValidationResult lengthCheck = validateLength(password);
        if (!lengthCheck.valid()) {
            return lengthCheck;
        }

        ValidationResult characterCheck = validateCharacterRequirements(password);
        if (!characterCheck.valid()) {
            return characterCheck;
        }

        if (isWeakPassword(password)) {
            return ValidationResult.invalidResult("Password is too common or contains sequential patterns");
        }

        return ValidationResult.validResult();
    }

    private static ValidationResult validateLength(String password) {
        if (password.length() < MIN_LENGTH) {
            return ValidationResult.invalidResult("Password must be at least " + MIN_LENGTH + " characters");
        }
        if (password.length() > MAX_LENGTH) {
            return ValidationResult.invalidResult("Password must not exceed " + MAX_LENGTH + " characters");
        }
        return ValidationResult.validResult();
    }

    private static ValidationResult validateCharacterRequirements(String password) {
        boolean hasUpper = UPPERCASE.matcher(password).find();
        boolean hasLower = LOWERCASE.matcher(password).find();
        boolean hasDigit = DIGIT.matcher(password).find();
        boolean hasSpecial = SPECIAL.matcher(password).find();

        List<String> errors = new ArrayList<>();
        if (!hasUpper) {
            errors.add("uppercase letter");
        }
        if (!hasLower) {
            errors.add("lowercase letter");
        }
        if (!hasDigit) {
            errors.add("digit");
        }
        if (!hasSpecial) {
            errors.add("special character");
        }

        if (!errors.isEmpty()) {
            return ValidationResult.invalidResult("Password must contain: " + String.join(", ", errors));
        }
        return ValidationResult.validResult();
    }

    public static boolean isWeakPassword(@Nullable String password) {
        if (password == null) {
            return true;
        }

        String lower = RbStringUtil.toLowerCase(password);
        if (lower == null || lower.length() < MIN_LENGTH) {
            return true;
        }

        for (String weak : WEAK_PASSWORDS) {
            if (lower.contains(weak)) {
                return true;
            }
        }

        return SEQUENTIAL_NUMBERS.matcher(lower).find() || hasSequentialLetters(lower);
    }

    private static boolean hasSequentialLetters(String str) {
        for (int i = 0; i < str.length() - 2; i++) {
            char c1 = str.charAt(i);
            char c2 = str.charAt(i + 1);
            char c3 = str.charAt(i + 2);
            if (c1 >= 'a' && c1 <= 'x' && c2 == c1 + 1 && c3 == c1 + 2) {
                return true;
            }
        }
        return false;
    }

    public record ValidationResult(
            boolean valid,
            String message,
            int length,
            boolean hasUppercase,
            boolean hasLowercase,
            boolean hasDigit,
            boolean hasSpecialChar) {

        public static ValidationResult validResult() {
            return new ValidationResult(true, "Valid", 0, true, true, true, true);
        }

        public static ValidationResult invalidResult(String message) {
            return new ValidationResult(false, message, 0, false, false, false, false);
        }

        public static ValidationResult of(
                boolean valid,
                String message,
                int length,
                boolean hasUpper,
                boolean hasLower,
                boolean hasDigit,
                boolean hasSpecial) {
            return new ValidationResult(valid, message, length, hasUpper, hasLower, hasDigit, hasSpecial);
        }
    }
}
