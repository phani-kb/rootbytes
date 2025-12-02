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

import com.github.phanikb.rootbytes.dto.v1.request.ApprovalRequest;
import com.github.phanikb.rootbytes.dto.v1.response.ApprovalResponse;
import com.github.phanikb.rootbytes.entity.Approval;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ApprovalMapper {

    @Mapping(source = "recipe.id", target = "recipeId")
    @Mapping(source = "approver.id", target = "approverId")
    @Mapping(source = "approver.publicName", target = "approverName")
    ApprovalResponse toResponse(Approval approval);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    @Mapping(target = "approver", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    Approval toEntity(ApprovalRequest request);
}
