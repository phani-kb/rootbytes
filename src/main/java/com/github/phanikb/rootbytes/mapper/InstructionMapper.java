/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.github.phanikb.rootbytes.dto.v1.request.InstructionRequest;
import com.github.phanikb.rootbytes.dto.v1.response.InstructionResponse;
import com.github.phanikb.rootbytes.entity.Instruction;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface InstructionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    @Named("toInstruction")
    Instruction toEntity(InstructionRequest request);

    @Named("toInstructionResponse")
    InstructionResponse toResponse(Instruction instruction);
}
