/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDietaryInfoRequest {
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isDairyFree;
    private Boolean isGlutenFree;
    private Boolean hasNuts;
    private Boolean hasOnion;
    private Boolean hasGarlic;
    private Boolean hasEggs;
    private Boolean hasSoy;
    private Boolean hasShellfish;
}
