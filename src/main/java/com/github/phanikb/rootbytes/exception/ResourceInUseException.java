/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.exception;

import java.io.Serial;

public class ResourceInUseException extends RbException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceInUseException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s is in use with %s : '%s'", resourceName, fieldName, fieldValue));
    }

    public ResourceInUseException(String message) {
        super(message);
    }

    public ResourceInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}
