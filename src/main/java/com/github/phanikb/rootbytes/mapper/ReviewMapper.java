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

import com.github.phanikb.rootbytes.dto.v1.request.ReviewRequest;
import com.github.phanikb.rootbytes.dto.v1.response.ReviewResponse;
import com.github.phanikb.rootbytes.entity.Review;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ReviewMapper {

    @Mapping(source = "recipe.id", target = "recipeId")
    @Mapping(source = "reviewer.id", target = "reviewerId")
    @Mapping(source = "reviewer.publicName", target = "reviewerName")
    ReviewResponse toResponse(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    @Mapping(target = "reviewer", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    Review toEntity(ReviewRequest request);
}
