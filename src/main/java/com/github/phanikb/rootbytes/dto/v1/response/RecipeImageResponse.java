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

import com.github.phanikb.rootbytes.enums.recipe.RecipeImageApprovalStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeImageResponse {
    private UUID id;
    private UUID recipeId;
    private String fileName;
    private String filePath;
    private String thumbnailPath;
    private Long fileSize;
    private String mimeType;
    private Integer width;
    private Integer height;
    private String caption;
    private RecipeImageApprovalStatus approvalStatus;
    private Instant approvedAt;
    private UUID approvedById;
    private String rejectedReason;
    private Boolean isPrimary;
    private Integer orderIndex;
    private Instant uploadedAt;
    private UUID uploadedById;
    private Instant updatedAt;
}
