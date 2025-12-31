/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.github.phanikb.rootbytes.dto.v1.request.RecipeImageUploadRequest;
import com.github.phanikb.rootbytes.dto.v1.response.RecipeImageUploadResponse;
import com.github.phanikb.rootbytes.entity.RecipeImage;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface RecipeImageUploadMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "storedName", ignore = true)
    @Mapping(target = "filePath", ignore = true)
    @Mapping(target = "thumbnailPath", ignore = true)
    @Mapping(target = "fileSize", ignore = true)
    @Mapping(target = "mimeType", ignore = true)
    @Mapping(target = "width", ignore = true)
    @Mapping(target = "height", ignore = true)
    @Mapping(target = "approvalStatus", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "rejectedReason", ignore = true)
    @Mapping(target = "uploadedAt", ignore = true)
    @Mapping(target = "uploadedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "uploadIp", ignore = true)
    @Mapping(target = "moderations", ignore = true)
    RecipeImage toEntity(RecipeImageUploadRequest request);

    @Mapping(source = "recipe.id", target = "recipeId")
    @Mapping(source = "approvedBy.id", target = "approvedById")
    @Mapping(source = "uploadedBy.id", target = "uploadedById")
    RecipeImageUploadResponse toResponse(RecipeImage recipeImage);
}
