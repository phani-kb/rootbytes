/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.response;

import java.time.Instant;
import java.util.UUID;

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
    private String lastName;
    private String description;
    private String category;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
    private String aliases; // comma separated aliases
}
