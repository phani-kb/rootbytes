/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.github.phanikb.rootbytes.dto.v1.response.InvitationCodeResponse;
import com.github.phanikb.rootbytes.entity.InvitationCode;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface InvitationCodeMapper {

    InvitationCodeResponse toResponse(InvitationCode invitationCode);
}
