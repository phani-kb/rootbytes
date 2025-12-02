/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.DESCRIPTION_REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.MESSAGE_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.MUST_BE_POSITIVE_OR_ZERO;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_XL;

@Builder
@Getter
@Setter
public class InstructionRequest {
    @NotNull(message = "Step number" + REQUIRED)
    @PositiveOrZero(message = "Step number" + MUST_BE_POSITIVE_OR_ZERO)
    private Integer stepNumber;

    @NotBlank(message = DESCRIPTION_REQUIRED)
    @Size(max = SIZE_XL, message = MESSAGE_TOO_LONG)
    private String description;

    @PositiveOrZero(message = "Duration" + MUST_BE_POSITIVE_OR_ZERO)
    private Integer durationMinutes;
}
