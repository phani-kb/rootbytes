/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.dto.v1.request.ReviewRequest;
import com.github.phanikb.rootbytes.entity.Recipe;
import com.github.phanikb.rootbytes.entity.Review;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.ModerationStatus;
import com.github.phanikb.rootbytes.exception.DuplicateResourceException;
import com.github.phanikb.rootbytes.exception.RecipeNotFoundException;
import com.github.phanikb.rootbytes.exception.ReviewNotFoundException;
import com.github.phanikb.rootbytes.repository.RecipeRepository;
import com.github.phanikb.rootbytes.repository.ReviewRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RecipeRepository recipeRepository;

    @Transactional
    public Review submitReview(UUID recipeId, ReviewRequest request, UserEntity reviewer) {
        log.info("Submitting review for recipe: {}", recipeId);

        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException(recipeId));

        if (reviewRepository.existsByRecipeAndReviewer(recipe, reviewer)) {
            throw new DuplicateResourceException("Review already exists for this recipe by this reviewer");
        }

        Review review = Review.builder()
                .recipe(recipe)
                .reviewer(reviewer)
                .comments(request.getComments())
                .status(ModerationStatus.PENDING)
                .build();

        return reviewRepository.save(review);
    }

    @Transactional
    public Review updateReview(UUID reviewId, ReviewRequest request, UserEntity user) {
        log.info("Updating review: {}", reviewId);

        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));

        review.setComments(request.getComments());

        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public Review getReviewById(UUID reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
    }
}
