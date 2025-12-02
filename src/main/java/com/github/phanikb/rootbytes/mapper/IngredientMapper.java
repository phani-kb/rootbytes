/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.github.phanikb.rootbytes.dto.v1.request.IngredientRequest;
import com.github.phanikb.rootbytes.dto.v1.response.IngredientResponse;
import com.github.phanikb.rootbytes.entity.Ingredient;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface IngredientMapper {

    @org.mapstruct.Mapping(source = "unit.id", target = "unitId")
    @org.mapstruct.Named("toIngredientResponse")
    IngredientResponse toResponse(Ingredient ingredient);

    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "recipe", ignore = true)
    @org.mapstruct.Mapping(target = "unit", ignore = true)
    @org.mapstruct.Named("toIngredient")
    Ingredient toEntity(IngredientRequest request);
}
