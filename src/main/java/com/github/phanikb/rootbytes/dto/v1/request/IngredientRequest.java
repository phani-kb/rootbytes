/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.MUST_BE_POSITIVE;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.MUST_BE_POSITIVE_OR_ZERO;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.NAME_REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.NAME_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.NOTES_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_L;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_XL;

@Builder
@Getter
@Setter
public class IngredientRequest {
    @NotBlank(message = NAME_REQUIRED)
    @Size(max = SIZE_L, message = NAME_TOO_LONG)
    private String name;

    @Positive(message = "Quantity" + MUST_BE_POSITIVE)
    private Double quantity;

    @NotNull(message = "Unit ID" + REQUIRED)
    private UUID unitId;

    @Size(max = SIZE_XL, message = NOTES_TOO_LONG)
    private String notes;

    @NotNull(message = "Order index" + REQUIRED)
    @PositiveOrZero(message = "Order index" + MUST_BE_POSITIVE_OR_ZERO)
    private Integer orderIndex;
}
