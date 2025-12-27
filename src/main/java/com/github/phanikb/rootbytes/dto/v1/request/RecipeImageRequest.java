/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.dto.v1.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.github.phanikb.rootbytes.validation.ValidImage;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeImageRequest {
    @NotNull(message = "Recipe ID is required")
    private UUID recipeId;

    @NotNull(message = "Image file is required")
    @ValidImage(
            maxSize = 1 * 1024 * 1024,
            allowedTypes = {"image/jpeg", "image/png", "image/webp"})
    private MultipartFile image;

    @Size(max = 500, message = "Caption cannot exceed 500 characters")
    private String caption;

    private Boolean isPrimary;

    @PositiveOrZero(message = "Order index must be zero or positive")
    private Integer orderIndex;

    private UUID uploaderId; // authenticated user
}
