/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.github.phanikb.rootbytes.dto.v1.request.InstructionRequest;
import com.github.phanikb.rootbytes.dto.v1.response.InstructionResponse;
import com.github.phanikb.rootbytes.entity.Instruction;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface InstructionMapper {

    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "recipe", ignore = true)
    @org.mapstruct.Named("toInstruction")
    Instruction toEntity(InstructionRequest request);

    @org.mapstruct.Named("toInstructionResponse")
    InstructionResponse toResponse(Instruction instruction);
}
