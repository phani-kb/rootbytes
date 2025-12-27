/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.validation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

public class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    private long maxSize;
    private Set<String> allowedTypes = new HashSet<>();

    @Override
    public void initialize(ValidImage constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
        this.allowedTypes = Arrays.stream(constraintAnnotation.allowedTypes()).collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        if (file.getSize() > maxSize) {
            context.buildConstraintViolationWithTemplate(
                    String.format("File size exceeds maximum allowed size of %d MB", maxSize / (1024 * 1024)))
                    .addConstraintViolation();
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase(Locale.ROOT))) {
            context.buildConstraintViolationWithTemplate(
                    String.format("Invalid file type. Allowed types: %s", String.join(", ", allowedTypes)))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
