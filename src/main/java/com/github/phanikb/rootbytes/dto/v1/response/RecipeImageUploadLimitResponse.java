/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeImageUploadLimitResponse {
    private Long totalUploaded;
    private Long approvedCount;
    private Long pendingCount;
    private Long rejectedCount;
    private Long maxAllowed;
    private Boolean canUploadMore;
    private Long remainingSlots;
}
