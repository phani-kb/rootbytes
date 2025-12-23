/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.dto.v1.request.ReviewRequest;
import com.github.phanikb.rootbytes.dto.v1.response.RbApiResponse;
import com.github.phanikb.rootbytes.dto.v1.response.ReviewResponse;
import com.github.phanikb.rootbytes.entity.Review;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.mapper.ReviewMapper;
import com.github.phanikb.rootbytes.service.ReviewService;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @PostMapping("/recipe/{recipeId}")
    public ResponseEntity<RbApiResponse<ReviewResponse>> submitReview(
            @PathVariable UUID recipeId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserEntity user) {
        log.info("Submitting review for recipe: {}", recipeId);

        Review review = reviewService.submitReview(recipeId, request, user);
        ReviewResponse response = reviewMapper.toResponse(review);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RbApiResponse.success("Review submitted successfully", response));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<RbApiResponse<ReviewResponse>> updateReview(
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserEntity user) {
        log.info("Updating review: {}", reviewId);

        Review review = reviewService.updateReview(reviewId, request, user);
        ReviewResponse response = reviewMapper.toResponse(review);

        return ResponseEntity.ok(RbApiResponse.success("Review updated successfully", response));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<RbApiResponse<ReviewResponse>> getReview(@PathVariable UUID reviewId) {
        log.debug("Fetching review: {}", reviewId);

        Review review = reviewService.getReviewById(reviewId);
        ReviewResponse response = reviewMapper.toResponse(review);

        return ResponseEntity.ok(RbApiResponse.success("Review retrieved successfully", response));
    }
}
