/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.exception;

import java.io.Serial;
import java.util.UUID;

public class ApprovalNotFoundException extends ResourceNotFoundException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ApprovalNotFoundException(UUID id) {
        super("Approval", "id", id);
    }

    public ApprovalNotFoundException(String field, Object value) {
        super("Approval", field, value);
    }

    public ApprovalNotFoundException(String message) {
        super(message);
    }
}
