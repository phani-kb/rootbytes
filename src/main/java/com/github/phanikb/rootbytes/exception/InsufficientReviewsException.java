/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.exception;

import java.io.Serial;

public class InsufficientReviewsException extends RbException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InsufficientReviewsException(String message) {
        super(message);
    }

    public InsufficientReviewsException(int required, int actual) {
        super(String.format("Insufficient reviews for approval. Required: %d, Actual: %d", required, actual));
    }
}
