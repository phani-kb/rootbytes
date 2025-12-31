/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.enums.ModerationAction;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeImageModerationRequest {
    @NotNull(message = "Image ID is required")
    private UUID imageId;

    @NotNull(message = "Action is required")
    private ModerationAction action;

    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;

    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;

    private UUID moderatorId;
}
