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

import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.COMMENTS_REQUIRED;
import static com.github.phanikb.rootbytes.common.ValidationConstants.Messages.COMMENTS_TOO_LONG;
import static com.github.phanikb.rootbytes.common.ValidationConstants.SIZE_XL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @NotBlank(message = COMMENTS_REQUIRED)
    @Size(max = SIZE_XL, message = COMMENTS_TOO_LONG)
    private String comments;

    private String status;
}
