/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.util;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;

public final class RbPathUtil {

    private static final String INVALID_CHARS_REGEX = "[\\x00-\\x1F<>:\"|?*]";
    private static final Pattern WINDOWS_RESERVED_PATTERN =
            Pattern.compile("^(CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\.|$)", Pattern.CASE_INSENSITIVE);

    private RbPathUtil() {}

    public static Path validateStoredPath(Path basePath, String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            throw new IllegalArgumentException("Stored path cannot be null or blank");
        }

        try {
            Path path = Path.of(storedPath).normalize();
            Path base = basePath.toAbsolutePath().normalize();

            if (!path.startsWith(base)) {
                throw new SecurityException("Stored path is outside base: " + storedPath);
            }

            return path;
        } catch (InvalidPathException e) {
            throw new SecurityException("Invalid stored path: " + storedPath, e);
        }
    }

    public static Path createSafeRecipeImagePath(Path basePath, UUID recipeId) {
        if (recipeId == null) {
            throw new IllegalArgumentException("Recipe ID cannot be null");
        }

        return basePath.toAbsolutePath().normalize().resolve("recipes").resolve(recipeId.toString());
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    public static String sanitizePathComponent(String component) {
        if (component == null || component.isBlank()) {
            throw new IllegalArgumentException("Path component cannot be null or blank");
        }

        if (component.indexOf('\0') >= 0) {
            throw new SecurityException("Path component contains invalid null byte: " + component);
        }

        String normalized = Normalizer.normalize(component, Normalizer.Form.NFKC);

        if (normalized.contains("..")) {
            throw new SecurityException("Path traversal detected in: " + component);
        }

        String sanitized = normalized
                .replaceAll(INVALID_CHARS_REGEX, "")
                .replaceAll("\\.{2,}", ".")
                .trim();

        if (WINDOWS_RESERVED_PATTERN.matcher(sanitized).find()) {
            sanitized = "_" + sanitized;
        }

        if (sanitized.isBlank()) {
            throw new SecurityException("Path component is empty after sanitization");
        }

        return sanitized;
    }
}
