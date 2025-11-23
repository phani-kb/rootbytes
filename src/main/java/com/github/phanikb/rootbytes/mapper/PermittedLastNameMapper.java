/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.github.phanikb.rootbytes.dto.response.PermittedLastNameResponse;
import com.github.phanikb.rootbytes.entity.LastNameAlias;
import com.github.phanikb.rootbytes.entity.PermittedLastName;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface PermittedLastNameMapper {

    @Mapping(target = "aliases", source = "aliases", qualifiedByName = "aliasesToString")
    PermittedLastNameResponse toResponse(PermittedLastName entity);

    @Named("aliasesToString")
    default String aliasesToString(List<LastNameAlias> aliases) {
        if (aliases == null || aliases.isEmpty()) {
            return null;
        }
        return aliases.stream().map(LastNameAlias::getAlias).collect(Collectors.joining(", "));
    }
}
