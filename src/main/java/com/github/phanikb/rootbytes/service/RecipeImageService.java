/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.tika.Tika;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.common.Constants;
import com.github.phanikb.rootbytes.config.RecipeImageUploadConfig;
import com.github.phanikb.rootbytes.dto.v1.request.RecipeImageModerationRequest;
import com.github.phanikb.rootbytes.dto.v1.request.RecipeImageUploadRequest;
import com.github.phanikb.rootbytes.dto.v1.response.RecipeImageModerationResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RecipeImageUploadLimitResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RecipeImageUploadResponse;
import com.github.phanikb.rootbytes.entity.Recipe;
import com.github.phanikb.rootbytes.entity.RecipeImage;
import com.github.phanikb.rootbytes.entity.RecipeImageModeration;
import com.github.phanikb.rootbytes.entity.RecipeImageUploadTracking;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.ModerationAction;
import com.github.phanikb.rootbytes.enums.recipe.RecipeImageApprovalStatus;
import com.github.phanikb.rootbytes.exception.FileSizeExceededException;
import com.github.phanikb.rootbytes.exception.FileUploadException;
import com.github.phanikb.rootbytes.exception.InvalidFileTypeException;
import com.github.phanikb.rootbytes.exception.MaxImagesExceededException;
import com.github.phanikb.rootbytes.exception.RecipeNotFoundException;
import com.github.phanikb.rootbytes.exception.ResourceNotFoundException;
import com.github.phanikb.rootbytes.exception.UnauthorizedAccessException;
import com.github.phanikb.rootbytes.exception.UserNotFoundException;
import com.github.phanikb.rootbytes.mapper.RecipeImageModerationMapper;
import com.github.phanikb.rootbytes.mapper.RecipeImageUploadMapper;
import com.github.phanikb.rootbytes.repository.RecipeImageModerationRepository;
import com.github.phanikb.rootbytes.repository.RecipeImageRepository;
import com.github.phanikb.rootbytes.repository.RecipeImageUploadTrackingRepository;
import com.github.phanikb.rootbytes.repository.RecipeRepository;
import com.github.phanikb.rootbytes.repository.UserRepository;
import com.github.phanikb.rootbytes.util.RbPathUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeImageService {

    private final RecipeImageRepository imageRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeImageModerationRepository moderationRepository;
    private final RecipeImageUploadTrackingRepository trackingRepository;
    private final RecipeImageProcessingService imageProcessingService;
    private final RecipeImageUploadMapper imageMapper;
    private final RecipeImageModerationMapper moderationMapper;
    private final RecipeImageUploadConfig config;
    private final Tika tika = new Tika();

    @Transactional
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public RecipeImageUploadResponse uploadImage(
            UUID recipeId, MultipartFile file, RecipeImageUploadRequest request, UUID uploaderId) {
        log.info("Uploading image for recipe {} by user {}", recipeId, uploaderId);

        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException(recipeId));

        UserEntity uploader =
                userRepository.findById(uploaderId).orElseThrow(() -> new UserNotFoundException(uploaderId));

        long currentImageCount = imageRepository.countByRecipeId(recipeId);
        if (currentImageCount >= config.getMaxImagesPerRecipe()) {
            throw new MaxImagesExceededException(
                    String.format("Maximum %d images allowed per recipe", config.getMaxImagesPerRecipe()));
        }

        validateImage(file);

        try {
            String storedName = UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = storedName + ".jpg";
            }

            RecipeImageProcessingService.ProcessedImage processed = imageProcessingService.processImage(
                    file.getInputStream(), originalFilename, uploader.getLastName());

            RecipeImageProcessingService.SavedImagePaths paths =
                    imageProcessingService.saveImage(processed, storedName, recipeId);

            Path basePath = Path.of(config.getBasePath()).toAbsolutePath().normalize();
            Path validatedPath = RbPathUtil.validateStoredPath(basePath, paths.getStandardPath());
            long processedSize = Files.size(validatedPath);

            Integer orderIndex = imageRepository
                    .findTopByRecipeIdOrderByOrderIndexDesc(recipeId)
                    .map(ri -> ri.getOrderIndex() + 1)
                    .orElse(0);

            boolean isAuthor = recipe.getAuthor().getId().equals(uploaderId);
            RecipeImageApprovalStatus status =
                    isAuthor ? RecipeImageApprovalStatus.APPROVED : RecipeImageApprovalStatus.PENDING;

            RecipeImage image = RecipeImage.builder()
                    .recipe(recipe)
                    .fileName(originalFilename)
                    .storedName(storedName)
                    .filePath(paths.getStandardPath())
                    .thumbnailPath(paths.getThumbnailPath())
                    .fileSize(processedSize)
                    .mimeType("image/jpeg")
                    .width(processed.getWidth())
                    .height(processed.getHeight())
                    .caption(request != null ? request.getCaption() : null)
                    .approvalStatus(status)
                    .approvedAt(isAuthor ? Instant.now() : null)
                    .approvedBy(isAuthor ? uploader : null)
                    .isPrimary(currentImageCount == 0 && isAuthor)
                    .orderIndex(orderIndex)
                    .uploadedBy(uploader)
                    .build();

            RecipeImage saved = imageRepository.save(image);

            updateUploadTracking(recipeId, uploaderId, status);

            log.info("Image uploaded successfully: {}, status: {}", saved.getId(), status);

            return imageMapper.toResponse(saved);

        } catch (IOException e) {
            log.error("Failed to process image upload", e);
            throw new FileUploadException("Failed to process image upload", e);
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        if (file.getSize() > config.getMaxImageSizeBytes()) {
            throw new FileSizeExceededException(
                    String.format("File size exceeds maximum allowed (%d bytes)", config.getMaxImageSizeBytes()));
        }

        String detectedMimeType;
        try {
            detectedMimeType = tika.detect(file.getInputStream());
            log.debug("Detected MIME type: {}", detectedMimeType);
        } catch (IOException e) {
            throw new FileUploadException("Failed to validate file type", e);
        }

        if (!config.getAllowedMimeTypes().contains(detectedMimeType.toLowerCase(Locale.ROOT))) {
            throw new InvalidFileTypeException(String.format(
                    "File type %s not allowed. Allowed types: %s", detectedMimeType, config.getAllowedMimeTypes()));
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !hasValidImageExtension(filename)) {
            throw new InvalidFileTypeException("Invalid image file extension");
        }
    }

    private boolean hasValidImageExtension(String filename) {
        String lower = filename.toLowerCase(Locale.ROOT);
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp");
    }

    @Transactional(readOnly = true)
    public List<RecipeImageUploadResponse> getApprovedImages(UUID recipeId) {
        return imageRepository
                .findByRecipeIdAndApprovalStatusOrderByOrderIndexAsc(recipeId, RecipeImageApprovalStatus.APPROVED)
                .stream()
                .map(imageMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecipeImageUploadResponse> getImagesByStatus(
            UUID recipeId, RecipeImageApprovalStatus status, UUID requesterId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException(recipeId));

        if (status != RecipeImageApprovalStatus.APPROVED
                && !recipe.getAuthor().getId().equals(requesterId)) {
            throw new UnauthorizedAccessException("Only recipe author can view non-approved images");
        }

        return imageRepository.findByRecipeIdAndApprovalStatusOrderByUploadedAtDesc(recipeId, status).stream()
                .map(imageMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<RecipeImageUploadResponse> getPendingImages(Pageable pageable) {
        return imageRepository
                .findByApprovalStatusOrderByUploadedAtAsc(RecipeImageApprovalStatus.PENDING, pageable)
                .map(imageMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public RecipeImageUploadResponse getImageById(UUID imageId, UUID requesterId) {
        RecipeImage image = imageRepository
                .findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.IMAGE_NOT_FOUND));

        if (!image.isApproved() && !image.getRecipe().getAuthor().getId().equals(requesterId)) {
            throw new UnauthorizedAccessException("Only recipe author can view non-approved images");
        }

        return imageMapper.toResponse(image);
    }

    @Transactional(readOnly = true)
    public List<RecipeImage> getRecipeImages(UUID recipeId) {
        return imageRepository.findByRecipeIdOrderByOrderIndexAsc(recipeId);
    }

    @Transactional
    public RecipeImageModerationResponse moderateImage(RecipeImageModerationRequest request) {
        RecipeImage image = imageRepository
                .findById(request.getImageId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.IMAGE_NOT_FOUND));

        UserEntity moderator = userRepository
                .findById(request.getModeratorId())
                .orElseThrow(() -> new UserNotFoundException(request.getModeratorId()));

        if (!image.getRecipe().getAuthor().getId().equals(request.getModeratorId())) {
            throw new UnauthorizedAccessException("Only recipe author can moderate images");
        }

        RecipeImageApprovalStatus previousStatus = image.getApprovalStatus();
        RecipeImageApprovalStatus newStatus = determineNewStatus(request.getAction(), previousStatus);

        image.setApprovalStatus(newStatus);
        if (newStatus == RecipeImageApprovalStatus.APPROVED) {
            image.setApprovedAt(Instant.now());
            image.setApprovedBy(moderator);
        } else if (newStatus == RecipeImageApprovalStatus.REJECTED) {
            image.setRejectedReason(request.getReason());
        }

        imageRepository.save(image);

        RecipeImageModeration moderation = RecipeImageModeration.builder()
                .image(image)
                .moderator(moderator)
                .action(request.getAction())
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .reason(request.getReason())
                .notes(request.getNotes())
                .build();

        RecipeImageModeration savedModeration = moderationRepository.save(moderation);

        updateTrackingForModeration(
                image.getRecipe().getId(), image.getUploadedBy().getId(), previousStatus, newStatus);

        log.info("Image {} moderated: {} -> {} by {}", image.getId(), previousStatus, newStatus, moderator.getId());

        return moderationMapper.toResponse(savedModeration);
    }

    private RecipeImageApprovalStatus determineNewStatus(
            ModerationAction action, RecipeImageApprovalStatus currentStatus) {
        return switch (action) {
            case APPROVE -> RecipeImageApprovalStatus.APPROVED;
            case REJECT -> RecipeImageApprovalStatus.REJECTED;
            case FLAG -> RecipeImageApprovalStatus.FLAGGED;
            case UNFLAG -> RecipeImageApprovalStatus.PENDING;
            default -> currentStatus;
        };
    }

    @Transactional
    public void deleteImage(UUID imageId, UUID userId) {
        log.info("Deleting image {} by user {}", imageId, userId);

        RecipeImage image = imageRepository
                .findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.IMAGE_NOT_FOUND));

        if (!image.getRecipe().getAuthor().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Only recipe author can delete images");
        }

        deleteImageFiles(image);

        imageRepository.delete(image);

        reorderImages(image.getRecipe().getId());

        log.info("Image deleted successfully: {}", imageId);
    }

    private void deleteImageFiles(RecipeImage image) {
        try {
            Path basePath = Path.of(config.getBasePath()).toAbsolutePath().normalize();
            Path standardPath = RbPathUtil.validateStoredPath(basePath, image.getFilePath());
            Path thumbnailPath = RbPathUtil.validateStoredPath(basePath, image.getThumbnailPath());

            if (Files.exists(standardPath)) {
                Files.delete(standardPath);
                log.debug("Deleted file: {}", standardPath);
            }

            if (Files.exists(thumbnailPath)) {
                Files.delete(thumbnailPath);
                log.debug("Deleted thumbnail: {}", thumbnailPath);
            }
        } catch (SecurityException e) {
            log.error("Security violation when deleting files for image {}: {}", image.getId(), e.getMessage());
        } catch (IOException e) {
            log.error("Failed to delete image files for image {}", image.getId(), e);
        }
    }

    @Transactional
    public void setPrimaryImage(UUID imageId, UUID userId) {
        RecipeImage image = imageRepository
                .findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.IMAGE_NOT_FOUND));

        if (!image.getRecipe().getAuthor().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Only recipe author can set primary image");
        }

        if (!image.isApproved()) {
            throw new IllegalStateException("Only approved images can be set as primary");
        }

        imageRepository.updatePrimaryFlag(image.getRecipe().getId(), false);

        image.setIsPrimary(true);
        imageRepository.save(image);
    }

    @Transactional
    public void reorderImages(UUID recipeId, List<UUID> imageIds, UUID userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException(recipeId));

        if (!recipe.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Only recipe author can reorder images");
        }

        for (int i = 0; i < imageIds.size(); i++) {
            UUID imageId = imageIds.get(i);
            RecipeImage image = imageRepository
                    .findById(imageId)
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.IMAGE_NOT_FOUND));

            if (!image.getRecipe().getId().equals(recipeId)) {
                throw new IllegalArgumentException("Image does not belong to this recipe");
            }

            image.setOrderIndex(i);
            imageRepository.save(image);
        }
    }

    private void reorderImages(UUID recipeId) {
        List<RecipeImage> images = imageRepository.findByRecipeIdOrderByOrderIndexAsc(recipeId);

        for (int i = 0; i < images.size(); i++) {
            images.get(i).setOrderIndex(i);
        }

        imageRepository.saveAll(images);
    }

    @Transactional(readOnly = true)
    public RecipeImageUploadLimitResponse getUploadLimits(UUID recipeId, UUID userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException(recipeId));
        log.debug("Getting upload limits for recipe: {} by user: {}", recipe.getTitle(), userId);

        RecipeImageUploadTracking tracking =
                trackingRepository.findByRecipeIdAndUserId(recipeId, userId).orElse(null);

        long totalUploaded = tracking != null ? tracking.getTotalUploads() : 0;
        long approvedCount = tracking != null ? tracking.getApprovedCount() : 0;
        long pendingCount = tracking != null ? tracking.getPendingCount() : 0;
        long rejectedCount = tracking != null ? tracking.getRejectedCount() : 0;

        long maxAllowed = config.getMaxImagesPerRecipe();
        long currentApprovedTotal =
                imageRepository.countByRecipeIdAndApprovalStatus(recipeId, RecipeImageApprovalStatus.APPROVED);
        long remainingSlots = Math.max(0, maxAllowed - currentApprovedTotal);

        return RecipeImageUploadLimitResponse.builder()
                .totalUploaded(totalUploaded)
                .approvedCount(approvedCount)
                .pendingCount(pendingCount)
                .rejectedCount(rejectedCount)
                .maxAllowed(maxAllowed)
                .canUploadMore(remainingSlots > 0)
                .remainingSlots(remainingSlots)
                .build();
    }

    private void updateUploadTracking(UUID recipeId, UUID userId, RecipeImageApprovalStatus status) {
        RecipeImageUploadTracking tracking = trackingRepository
                .findByRecipeIdAndUserId(recipeId, userId)
                .orElseGet(() -> {
                    Recipe recipe = recipeRepository.getReferenceById(recipeId);
                    UserEntity user = userRepository.getReferenceById(userId);
                    return RecipeImageUploadTracking.builder()
                            .recipe(recipe)
                            .user(user)
                            .approvedCount(0)
                            .pendingCount(0)
                            .rejectedCount(0)
                            .totalUploads(0)
                            .build();
                });

        tracking.setTotalUploads(tracking.getTotalUploads() + 1);
        tracking.setLastUploadAt(Instant.now());

        if (status == RecipeImageApprovalStatus.APPROVED) {
            tracking.setApprovedCount(tracking.getApprovedCount() + 1);
        } else {
            tracking.setPendingCount(tracking.getPendingCount() + 1);
        }

        trackingRepository.save(tracking);
    }

    private void updateTrackingForModeration(
            UUID recipeId, UUID uploaderId, RecipeImageApprovalStatus oldStatus, RecipeImageApprovalStatus newStatus) {
        if (oldStatus == newStatus) {
            return;
        }

        trackingRepository.findByRecipeIdAndUserId(recipeId, uploaderId).ifPresent(tracking -> {
            switch (oldStatus) {
                case PENDING -> tracking.setPendingCount(Math.max(0, tracking.getPendingCount() - 1));
                case APPROVED -> tracking.setApprovedCount(Math.max(0, tracking.getApprovedCount() - 1));
                case REJECTED -> tracking.setRejectedCount(Math.max(0, tracking.getRejectedCount() - 1));
                default -> {
                    /* no-op */
                }
            }

            switch (newStatus) {
                case PENDING -> tracking.setPendingCount(tracking.getPendingCount() + 1);
                case APPROVED -> tracking.setApprovedCount(tracking.getApprovedCount() + 1);
                case REJECTED -> tracking.setRejectedCount(tracking.getRejectedCount() + 1);
                default -> {
                    /* no-op */
                }
            }

            trackingRepository.save(tracking);
        });
    }
}
