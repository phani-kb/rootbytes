/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeImageUploadRequest {
    @Size(max = 500, message = "Caption cannot exceed 500 characters")
    private String caption;

    private Boolean isPrimary;

    @PositiveOrZero(message = "Order index must be zero or positive")
    private Integer orderIndex;
}
