/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import com.github.phanikb.rootbytes.config.RecipeImageUploadConfig;
import com.github.phanikb.rootbytes.util.RbPathUtil;
import com.github.phanikb.rootbytes.util.RbStringUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeImageProcessingService {
    private final RecipeImageUploadConfig config;

    private static final String IMAGE_FORMAT = "jpg";
    private static final String THUMBNAIL_SUFFIX = "_thumb";
    private static final String FONT_NAME = "Arial";
    private static final int FONT_PADDING = 10;

    public ProcessedImage processImage(InputStream inputStream, String originalFilename, String userLastName)
            throws IOException {

        log.debug("Processing image: {}", originalFilename);

        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            throw new IllegalArgumentException("Invalid or corrupted image file: " + originalFilename);
        }

        validateImageDimensions(originalImage);

        BufferedImage processedImage = resizeAndOptimize(originalImage);

        if (config.getWatermark().isEnabled()) {
            processedImage = addWatermark(processedImage, userLastName);
        }

        BufferedImage thumbnail = createThumbnail(processedImage);

        return ProcessedImage.builder()
                .standardImage(processedImage)
                .thumbnail(thumbnail)
                .width(processedImage.getWidth())
                .height(processedImage.getHeight())
                .build();
    }

    private void validateImageDimensions(BufferedImage image) {
        int maxDimension = Math.max(
                config.getStandard().getMaxWidth(), config.getStandard().getMaxHeight());

        if (image.getWidth() > maxDimension * 3 || image.getHeight() > maxDimension * 3) {
            throw new IllegalArgumentException(String.format(
                    "Image too large: %dx%d (max: %dx%d)",
                    image.getWidth(), image.getHeight(), maxDimension * 3, maxDimension * 3));
        }
    }

    private BufferedImage resizeAndOptimize(BufferedImage original) throws IOException {
        return Thumbnails.of(original)
                .size(config.getStandard().getMaxWidth(), config.getStandard().getMaxHeight())
                .outputFormat(IMAGE_FORMAT)
                .outputQuality(0.9)
                .asBufferedImage();
    }

    private BufferedImage addWatermark(BufferedImage image, String userLastName) {
        try {
            String watermarkText = buildWatermarkText(userLastName);
            BufferedImage watermarkImage = createWatermarkImage(watermarkText, image.getWidth());

            return Thumbnails.of(image)
                    .size(image.getWidth(), image.getHeight())
                    .watermark(
                            getWatermarkPosition(),
                            watermarkImage,
                            config.getWatermark().getOpacity())
                    .asBufferedImage();

        } catch (IOException e) {
            log.error("Failed to add watermark, returning original image", e);
            return image;
        }
    }

    private String buildWatermarkText(String userLastName) {
        if (userLastName == null || userLastName.isBlank()) {
            return config.getWatermark().getText();
        }
        return String.format("%s | %s", config.getWatermark().getText(), userLastName);
    }

    private BufferedImage createThumbnail(BufferedImage image) throws IOException {
        return Thumbnails.of(image)
                .size(config.getThumbnail().getWidth(), config.getThumbnail().getHeight())
                .crop(Positions.CENTER)
                .outputFormat(IMAGE_FORMAT)
                .outputQuality(0.85)
                .asBufferedImage();
    }

    private BufferedImage createWatermarkImage(String text, int imageWidth) {
        int fontSize = calculateFontSize(imageWidth);
        Font font = new Font(FONT_NAME, Font.BOLD, fontSize);

        int[] dimensions = calculateTextDimensions(text, font);
        int textWidth = dimensions[0];
        int textHeight = dimensions[1];

        int padding = FONT_PADDING;
        int maxWatermarkWidth = Math.min(textWidth + (padding * 2), imageWidth);
        BufferedImage watermark =
                new BufferedImage(maxWatermarkWidth, textHeight + (padding * 2), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = watermark.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(font);

            g.setColor(new Color(0, 0, 0, 100));
            g.drawString(text, padding + 1, textHeight + 1);

            g.setColor(new Color(255, 255, 255, 200));
            g.drawString(text, padding, textHeight);
        } finally {
            g.dispose();
        }

        return watermark;
    }

    private int calculateFontSize(int imageWidth) {
        return Math.clamp(imageWidth / 30, 12, 48); // 12px min, 48px max
    }

    private int[] calculateTextDimensions(String text, Font font) {
        BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = temp.createGraphics();
        try {
            g2d.setFont(font);
            FontMetrics metrics = g2d.getFontMetrics();
            return new int[] {metrics.stringWidth(text), metrics.getHeight()};
        } finally {
            g2d.dispose();
        }
    }

    private Positions getWatermarkPosition() {
        String position = config.getWatermark().getPosition();
        return switch (position.toUpperCase(RbStringUtil.ROOT_LOCALE)) {
            case "TOP_LEFT" -> Positions.TOP_LEFT;
            case "TOP_RIGHT" -> Positions.TOP_RIGHT;
            case "BOTTOM_LEFT" -> Positions.BOTTOM_LEFT;
            case "CENTER" -> Positions.CENTER;
            default -> Positions.BOTTOM_RIGHT;
        };
    }

    public SavedImagePaths saveImage(ProcessedImage processed, String storedName, UUID recipeId) throws IOException {
        String safeName = RbPathUtil.sanitizePathComponent(storedName);

        Path basePath = Path.of(config.getBasePath()).toAbsolutePath().normalize();
        Path recipeDir = RbPathUtil.createSafeRecipeImagePath(basePath, recipeId);

        if (!Files.exists(recipeDir)) {
            Files.createDirectories(recipeDir);
        }

        Path standardPath = recipeDir.resolve(safeName + "." + IMAGE_FORMAT);
        Path thumbnailPath = recipeDir.resolve(safeName + THUMBNAIL_SUFFIX + "." + IMAGE_FORMAT);

        try {
            ImageIO.write(processed.getStandardImage(), IMAGE_FORMAT, standardPath.toFile());
            ImageIO.write(processed.getThumbnail(), IMAGE_FORMAT, thumbnailPath.toFile());
            log.info("Saved images to directory: {}", recipeDir);
        } catch (IOException e) {
            cleanupFailedSave(standardPath, thumbnailPath);
            throw e;
        }

        return SavedImagePaths.builder()
                .standardPath(standardPath.toString())
                .thumbnailPath(thumbnailPath.toString())
                .build();
    }

    private void cleanupFailedSave(Path... paths) {
        for (Path path : paths) {
            try {
                if (Files.exists(path)) {
                    Files.delete(path);
                }
            } catch (IOException e) {
                log.warn("Failed to cleanup file: {}", path, e);
            }
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class ProcessedImage {
        private BufferedImage standardImage;
        private BufferedImage thumbnail;
        private Integer width;
        private Integer height;
    }

    @lombok.Data
    @lombok.Builder
    public static class SavedImagePaths {
        private String standardPath;
        private String thumbnailPath;
    }
}
