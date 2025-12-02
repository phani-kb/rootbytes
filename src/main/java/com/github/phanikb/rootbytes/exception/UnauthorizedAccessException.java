/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.exception;

import java.io.Serial;

public class UnauthorizedAccessException extends RbException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String resourceType, String action) {
        super(String.format("You are not authorized to %s this %s", action, resourceType));
    }
}
