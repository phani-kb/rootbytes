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

import com.github.phanikb.rootbytes.dto.v1.request.RecipeImageModerationRequest;
import com.github.phanikb.rootbytes.dto.v1.response.RecipeImageModerationResponse;
import com.github.phanikb.rootbytes.entity.RecipeImageModeration;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface RecipeImageModerationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "moderator", ignore = true)
    @Mapping(target = "previousStatus", ignore = true)
    @Mapping(target = "newStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    RecipeImageModeration toEntity(RecipeImageModerationRequest request);

    @Mapping(source = "image.id", target = "imageId")
    @Mapping(source = "moderator.id", target = "moderatorId")
    @Mapping(source = "moderator.publicName", target = "moderatorName")
    RecipeImageModerationResponse toResponse(RecipeImageModeration moderation);
}
