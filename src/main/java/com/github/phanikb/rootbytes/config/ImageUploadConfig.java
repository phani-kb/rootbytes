/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "rootbytes.image.upload")
@Data
public class ImageUploadConfig {
    private String basePath;
    private int maxImagesPerRecipe;
    private long maxImageSizeBytes;
    private List<String> allowedMimeTypes;

    private ThumbnailConfig thumbnail;
    private StandardConfig standard;
    private WatermarkConfig watermark;

    @Data
    public static class ThumbnailConfig {
        private Integer width;
        private Integer height;
    }

    @Data
    public static class StandardConfig {
        private Integer maxWidth;
        private Integer maxHeight;
    }

    @Data
    public static class WatermarkConfig {
        private Boolean enabled;
        private String text;
        private Float opacity;
        private String position; // TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER
    }
}
