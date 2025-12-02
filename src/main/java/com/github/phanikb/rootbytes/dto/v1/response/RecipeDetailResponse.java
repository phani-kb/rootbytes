/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeDetailResponse {

    private UUID id;
    private UUID authorId;
    private String authorName;
    private String title;
    private String description;
    private String story;
    private Integer version;
    private String status;
    private Boolean isCurrentVersion;
    private Boolean isPrivate;
    private RecipeDietaryInfoResponse dietaryInfo;
    private Integer prepTimeMinutes;
    private Integer cookTimeMinutes;
    private Integer servings;
    private String difficulty;
    private String cuisine;
    private String category;
    private List<IngredientResponse> ingredients;
    private List<InstructionResponse> instructions;
    private Long likesCount;
    private Double averageRating;
    private Long viewsCount;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant publishedAt;
    private Boolean isMarkedAsFavorite;
    private Long favoriteCount;
}
