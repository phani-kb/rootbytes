/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.github.phanikb.rootbytes.dto.request.SystemConfigRequest;
import com.github.phanikb.rootbytes.dto.response.SystemConfigResponse;
import com.github.phanikb.rootbytes.entity.SystemConfig;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface SystemConfigMapper {

    @Mapping(target = "updatedAt", expression = "java(map(config.getUpdatedAt()))")
    SystemConfigResponse toResponse(SystemConfig config);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    SystemConfig toEntity(SystemConfigRequest request);

    default LocalDateTime map(Instant value) {
        return value == null ? null : LocalDateTime.ofInstant(value, ZoneId.systemDefault());
    }
}
