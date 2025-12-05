/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.exception;

import java.io.Serial;

public abstract class RbException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    protected RbException(String message) {
        super(message);
    }

    protected RbException(String message, Throwable cause) {
        super(message, cause);
    }
}
