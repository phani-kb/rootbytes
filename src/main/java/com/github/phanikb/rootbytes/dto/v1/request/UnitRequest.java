/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.enums.UnitType;

import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.ABBREVIATION_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.DESCRIPTION_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.NAME_REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.NAME_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.TYPE_REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_L;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_S;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_XS;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitRequest {

    @NotBlank(message = NAME_REQUIRED)
    @Size(max = SIZE_S, message = NAME_TOO_LONG)
    private String name;

    @NotBlank(message = "Abbreviation" + REQUIRED)
    @Size(max = SIZE_XS, message = ABBREVIATION_TOO_LONG)
    private String abbreviation;

    @NotNull(message = TYPE_REQUIRED)
    private UnitType unitType;

    @Size(max = SIZE_L, message = DESCRIPTION_TOO_LONG)
    private String description;
}
