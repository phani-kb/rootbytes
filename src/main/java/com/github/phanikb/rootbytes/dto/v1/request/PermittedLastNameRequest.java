/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.CATEGORY_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.DESCRIPTION_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.NAME_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_M;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_S;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermittedLastNameRequest {

    @NotBlank(message = "Last name" + REQUIRED)
    @Size(max = SIZE_M, message = NAME_TOO_LONG)
    private String lastName;

    @Size(max = SIZE_M, message = DESCRIPTION_TOO_LONG)
    private String description;

    @Size(max = SIZE_S, message = CATEGORY_TOO_LONG)
    private String category;

    private List<String> aliases;
}
