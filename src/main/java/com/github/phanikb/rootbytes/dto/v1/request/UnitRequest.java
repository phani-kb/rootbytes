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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitRequest {

    @NotBlank(message = "Unit name is required")
    @Size(max = 50, message = "Unit name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Unit abbreviation is required")
    @Size(max = 10, message = "Unit abbreviation must not exceed 10 characters")
    private String abbreviation;

    @NotNull(message = "Unit type is required")
    private UnitType unitType;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
}
