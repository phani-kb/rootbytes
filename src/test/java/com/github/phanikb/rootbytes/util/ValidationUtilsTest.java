/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationUtilsTest {

    @Test
    void shouldReturnTrueWhenValueInRange() {
        assertTrue(ValidationUtils.isInRange(5, 1, 10));
        assertTrue(ValidationUtils.isInRange(1, 1, 10));
        assertTrue(ValidationUtils.isInRange(10, 1, 10));
    }

    @Test
    void shouldReturnFalseWhenValueOutOfRange() {
        assertFalse(ValidationUtils.isInRange(0, 1, 10));
        assertFalse(ValidationUtils.isInRange(11, 1, 10));
        assertFalse(ValidationUtils.isInRange(-5, 1, 10));
    }

    @Test
    void shouldReturnFalseForNullValue() {
        assertFalse(ValidationUtils.isInRange(null, 1, 10));
    }
}
