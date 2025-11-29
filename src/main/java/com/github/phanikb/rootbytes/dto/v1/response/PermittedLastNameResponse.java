/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.response;

import java.time.Instant;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermittedLastNameResponse {
    private UUID id;

    @NotBlank
    private String lastName;

    @Nullable
    private String description;

    @Nullable
    private String category;

    private Boolean isActive;
    private Instant createdAt;

    @Nullable
    private Instant updatedAt;

    private String aliases; // comma separated aliases
}
