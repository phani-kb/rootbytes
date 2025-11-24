/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.util;

import org.jspecify.annotations.Nullable;

public final class LogSanitizer {

    private static final String[] REPLACE_CHARS = {"\r", "\n", "\t"};

    private LogSanitizer() {}

    public static String sanitize(@Nullable String message) {
        if (message == null) {
            return "null";
        }

        String result = message;
        for (String replaceChar : REPLACE_CHARS) {
            result = result.replace(replaceChar, " ");
        }
        return result.replaceAll("\\s+", " ").trim();
    }

    public static String sanitize(@Nullable Throwable throwable) {
        if (throwable == null) {
            return "No throwable";
        }
        return sanitize(throwable.getMessage());
    }

    public static String sanitizeAndTruncate(@Nullable String message, int maxLength) {
        String sanitized = sanitize(message);
        if (sanitized.length() <= maxLength) {
            return sanitized;
        }
        return sanitized.substring(0, maxLength - 3) + "...";
    }
}
