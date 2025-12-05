/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.exception;

import java.io.Serial;
import java.util.UUID;

public class ReviewNotFoundException extends ResourceNotFoundException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ReviewNotFoundException(UUID id) {
        super("Review", "id", id);
    }

    public ReviewNotFoundException(String fieldName, Object fieldValue) {
        super("Review", fieldName, fieldValue);
    }

    public ReviewNotFoundException(String message) {
        super(message);
    }
}
