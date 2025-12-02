/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.CONFIG_VALUE_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.DESCRIPTION_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.NAME_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_M;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_XL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigRequest {

    @Size(max = SIZE_M, message = NAME_TOO_LONG)
    private String keyName;

    @NotBlank(message = "Key value" + REQUIRED)
    @Size(max = SIZE_XL, message = CONFIG_VALUE_TOO_LONG)
    private String keyValue;

    @Size(max = SIZE_XL, message = DESCRIPTION_TOO_LONG)
    private String description;
}
