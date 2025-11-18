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

import com.github.phanikb.rootbytes.dto.request.UnitRequest;
import com.github.phanikb.rootbytes.dto.response.UnitResponse;
import com.github.phanikb.rootbytes.entity.Unit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface UnitMapper {

    @Mapping(source = "active", target = "isActive")
    UnitResponse toResponse(Unit unit);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Unit toEntity(UnitRequest request);

    UnitRequest toRequest(Unit unit);
}
