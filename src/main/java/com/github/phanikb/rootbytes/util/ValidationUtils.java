/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.util;

public final class ValidationUtils {

    private ValidationUtils() {}

    public static boolean isInRange(Integer value, int min, int max) {
        return value != null && value >= min && value <= max;
    }
}
