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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigRequest {

    @Size(max = 100, message = "Key name must not exceed 100 characters")
    private String keyName;

    @NotBlank(message = "Key value is required")
    @Size(max = 255, message = "Key value must not exceed 255 characters")
    private String keyValue;

    private String description;
}
