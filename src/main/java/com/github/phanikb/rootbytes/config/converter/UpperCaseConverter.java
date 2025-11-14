/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */
package com.github.phanikb.rootbytes.config.converter;

import java.util.Locale;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UpperCaseConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute != null ? attribute.trim().toUpperCase(Locale.ROOT) : null;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData;
    }
}
