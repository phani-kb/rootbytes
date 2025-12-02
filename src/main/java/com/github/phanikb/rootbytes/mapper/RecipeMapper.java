/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.mapper;

import java.util.List;
import java.util.UUID;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.github.phanikb.rootbytes.dto.v1.request.IngredientRequest;
import com.github.phanikb.rootbytes.dto.v1.request.InstructionRequest;
import com.github.phanikb.rootbytes.dto.v1.request.RecipeRequest;
import com.github.phanikb.rootbytes.dto.v1.response.IngredientResponse;
import com.github.phanikb.rootbytes.dto.v1.response.InstructionResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RecipeDetailResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RecipeDietaryInfoResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RecipeResponse;
import com.github.phanikb.rootbytes.entity.Ingredient;
import com.github.phanikb.rootbytes.entity.Instruction;
import com.github.phanikb.rootbytes.entity.Recipe;
import com.github.phanikb.rootbytes.entity.RecipeDietaryInfo;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN, uses = {
        IngredientMapper.class, InstructionMapper.class })
public interface RecipeMapper {
    @IterableMapping(qualifiedByName = "toIngredientResponse")
    List<IngredientResponse> mapIngredients(List<Ingredient> ingredients);

    @IterableMapping(qualifiedByName = "toInstructionResponse")
    List<InstructionResponse> mapInstructions(List<Instruction> instructions);

    @IterableMapping(qualifiedByName = "toIngredient")
    List<Ingredient> mapIngredientRequests(List<IngredientRequest> ingredients);

    @IterableMapping(qualifiedByName = "toInstruction")
    List<Instruction> mapInstructionRequests(List<InstructionRequest> instructions);

    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "favoriteCount", ignore = true)
    @Mapping(target = "isMarkedFavorite", ignore = true)
    RecipeResponse toResponse(Recipe recipe);

    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "favoriteCount", ignore = true)
    @Mapping(target = "isMarkedAsFavorite", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "viewsCount", ignore = true)
    RecipeDetailResponse toDetailResponse(Recipe recipe);

    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "difficulty", ignore = true)
    @Mapping(target = "isMarkedFavorite", ignore = true)
    @Mapping(target = "favoriteCount", ignore = true)
    RecipeResponse toResponse(Recipe recipe, UUID currentUserId);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isCurrentVersion", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "strikeCount", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "dietaryInfo", ignore = true)
    Recipe toEntity(RecipeRequest request);

    RecipeDietaryInfoResponse toDietaryInfoResponse(RecipeDietaryInfo dietaryInfo);
}
