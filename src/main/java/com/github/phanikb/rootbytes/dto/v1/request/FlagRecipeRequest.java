/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.enums.FlagReason;

import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.REASON_REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.REASON_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_L;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlagRecipeRequest {

    @NotNull(message = REASON_REQUIRED)
    private FlagReason reason;

    @Size(max = SIZE_L, message = REASON_TOO_LONG)
    private String description;
}
