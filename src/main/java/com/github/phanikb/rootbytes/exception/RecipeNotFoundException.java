/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.exception;

import java.io.Serial;
import java.util.UUID;

public class RecipeNotFoundException extends RbException {
    @Serial
    private static final long serialVersionUID = 1L;

    public RecipeNotFoundException(UUID id) {
        super("Recipe not found with id: " + id);
    }

    public RecipeNotFoundException(String fieldName, Object fieldValue) {
        super("Recipe not found with " + fieldName + ": " + fieldValue);
    }

    public RecipeNotFoundException(String message) {
        super(message);
    }
}
