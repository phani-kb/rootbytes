/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.exception;

import java.io.Serial;

public class InvalidLastNameException extends RbException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidLastNameException(String message) {
        super(message);
    }

    public InvalidLastNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
