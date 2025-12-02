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

import com.github.phanikb.rootbytes.enums.FlagReason;
import com.github.phanikb.rootbytes.enums.FlagStatus;

/** Response DTO for recipe flag information. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlagRecipeResponse {
    private UUID id;
    private UUID recipeId;
    private String recipeTitle;
    private UUID reportedById;
    private String reportedByName;
    private FlagReason reason;
    private String description;
    private FlagStatus status;
    private Instant createdAt;
    private Instant resolvedAt;
    private UUID resolvedById;
    private String resolvedByName;
}
