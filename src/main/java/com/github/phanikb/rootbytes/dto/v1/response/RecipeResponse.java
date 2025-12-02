/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RecipeResponse {
    private UUID id;
    private UUID authorId;
    private String title;
    private String description;
    private String story;
    private Integer version;
    private String status;
    private Boolean isCurrentVersion;
    private Boolean isPrivate;
    private Integer prepTimeMinutes;
    private Integer cookTimeMinutes;
    private Integer servings;
    private String difficulty;
    private String cuisine;
    private String category;
    private Boolean isMarkedFavorite;
    private Long favoriteCount;
    private List<IngredientResponse> ingredients;
    private List<InstructionResponse> instructions;
    private Instant createdAt;
    private Instant updatedAt;
}
