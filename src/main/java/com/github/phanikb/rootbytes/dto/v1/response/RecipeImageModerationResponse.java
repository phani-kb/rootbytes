/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.response;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.enums.ModerationAction;
import com.github.phanikb.rootbytes.enums.recipe.RecipeImageApprovalStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeImageModerationResponse {
    private UUID id;
    private UUID imageId;
    private UUID moderatorId;
    private String moderatorName;
    private ModerationAction action;
    private RecipeImageApprovalStatus previousStatus;
    private RecipeImageApprovalStatus newStatus;
    private String reason;
    private String notes;
    private Instant createdAt;
}
