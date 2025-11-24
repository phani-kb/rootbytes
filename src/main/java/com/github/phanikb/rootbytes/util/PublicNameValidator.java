/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.util;

import java.util.regex.Pattern;

import org.jspecify.annotations.Nullable;

public final class PublicNameValidator {

    private static final int MAX_LENGTH = 6;
    private static final Pattern VALID_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{0,5}$");

    private PublicNameValidator() {}

    public static ValidationResult validate(@Nullable String publicName) {
        if (publicName == null || publicName.isBlank()) {
            return ValidationResult.invalidResult("Public name cannot be empty");
        }

        if (publicName.length() > MAX_LENGTH) {
            return ValidationResult.invalidResult("Public name must not exceed " + MAX_LENGTH + " characters");
        }

        if (!VALID_PATTERN.matcher(publicName).matches()) {
            if (!Character.isLetter(publicName.charAt(0))) {
                return ValidationResult.invalidResult("Public name must start with a letter");
            }
            return ValidationResult.invalidResult("Public name can only contain letters and numbers");
        }

        return ValidationResult.validResult();
    }

    public record ValidationResult(boolean valid, String message) {

        public static ValidationResult validResult() {
            return new ValidationResult(true, "Valid");
        }

        public static ValidationResult invalidResult(String message) {
            return new ValidationResult(false, message);
        }
    }
}
