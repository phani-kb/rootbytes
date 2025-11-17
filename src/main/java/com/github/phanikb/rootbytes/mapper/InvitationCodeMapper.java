/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.github.phanikb.rootbytes.dto.response.InvitationCodeResponse;
import com.github.phanikb.rootbytes.entity.InvitationCode;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InvitationCodeMapper {

    InvitationCodeResponse toResponse(InvitationCode invitationCode);
}
