/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.response;

import java.time.Instant;
import java.util.UUID;

import jakarta.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.enums.UnitType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitResponse {
    private UUID id;
    private String name;

    private String abbreviation;

    private UnitType unitType;

    @Nullable
    private String description;

    private Boolean isActive;

    private Instant createdAt;

    private Instant updatedAt;
}
