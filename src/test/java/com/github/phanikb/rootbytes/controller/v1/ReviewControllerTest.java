/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller.v1;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.phanikb.rootbytes.dto.v1.request.ReviewRequest;
import com.github.phanikb.rootbytes.dto.v1.response.ReviewResponse;
import com.github.phanikb.rootbytes.entity.Recipe;
import com.github.phanikb.rootbytes.entity.Review;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.ModerationStatus;
import com.github.phanikb.rootbytes.mapper.ReviewMapper;
import com.github.phanikb.rootbytes.service.ReviewService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private ReviewMapper reviewMapper;

    private ReviewController controller;
    private UUID reviewId;
    private UUID recipeId;
    private UserEntity user;
    private Review review;
    private ReviewResponse response;

    @BeforeEach
    void setUp() {
        controller = new ReviewController(reviewService, reviewMapper);
        reviewId = UUID.randomUUID();
        recipeId = UUID.randomUUID();

        user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("reviewer@example.com")
                .lastName("Reviewer")
                .uniqueName("REVIEWER01")
                .build();

        Recipe recipe = Recipe.builder().id(recipeId).title("Test Recipe").build();

        review = Review.builder()
                .id(reviewId)
                .recipe(recipe)
                .reviewer(user)
                .status(ModerationStatus.APPROVED)
                .comments("Great recipe")
                .reviewedAt(Instant.now())
                .build();

        response = ReviewResponse.builder()
                .id(reviewId)
                .recipeId(recipeId)
                .reviewerId(user.getId())
                .reviewerName("Reviewer")
                .status("APPROVED")
                .comments("Great recipe")
                .reviewedAt(Instant.now())
                .build();
    }

    @Test
    void shouldSubmitReview() {
        var request =
                ReviewRequest.builder().status("APPROVED").comments("Excellent").build();

        when(reviewService.submitReview(eq(recipeId), any(ReviewRequest.class), eq(user)))
                .thenReturn(review);
        when(reviewMapper.toResponse(review)).thenReturn(response);

        var result = controller.submitReview(recipeId, request, user);

        assertEquals(201, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(response, result.getBody().getData());
    }

    @Test
    void shouldUpdateReview() {
        var request = ReviewRequest.builder()
                .status("REJECTED")
                .comments("Needs improvement")
                .build();

        when(reviewService.updateReview(eq(reviewId), any(ReviewRequest.class), eq(user)))
                .thenReturn(review);
        when(reviewMapper.toResponse(review)).thenReturn(response);

        var result = controller.updateReview(reviewId, request, user);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(response, result.getBody().getData());
    }

    @Test
    void shouldGetReview() {
        when(reviewService.getReviewById(reviewId)).thenReturn(review);
        when(reviewMapper.toResponse(review)).thenReturn(response);

        var result = controller.getReview(reviewId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(response, result.getBody().getData());
    }
}
