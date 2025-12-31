/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.exception;

public class MaxImagesExceededException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MaxImagesExceededException(String message) {
        super(message);
    }
}
