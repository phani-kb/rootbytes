/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.exception;

public class InvalidFileTypeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidFileTypeException(String message) {
        super(message);
    }
}
