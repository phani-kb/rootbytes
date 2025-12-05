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

import com.github.phanikb.rootbytes.dto.v1.request.ApprovalRequest;
import com.github.phanikb.rootbytes.entity.Approval;
import com.github.phanikb.rootbytes.entity.Recipe;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.ModerationStatus;
import com.github.phanikb.rootbytes.exception.ApprovalNotFoundException;
import com.github.phanikb.rootbytes.exception.DuplicateResourceException;
import com.github.phanikb.rootbytes.exception.RecipeNotFoundException;
import com.github.phanikb.rootbytes.repository.ApprovalRepository;
import com.github.phanikb.rootbytes.repository.RecipeRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final RecipeRepository recipeRepository;

    @Transactional
    public Approval submitApproval(UUID recipeId, ApprovalRequest request, UserEntity approver) {
        log.info("Submitting approval for recipe: {}", recipeId);

        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException(recipeId));

        if (approvalRepository.existsByRecipeAndApprover(recipe, approver)) {
            throw new DuplicateResourceException("Approval already exists for this recipe by this approver");
        }

        Approval approval = Approval.builder()
                .recipe(recipe)
                .approver(approver)
                .status(ModerationStatus.valueOf(request.getStatus()))
                .comments(request.getComments())
                .build();

        return approvalRepository.save(approval);
    }

    @Transactional
    public Approval updateApproval(UUID approvalId, ApprovalRequest request, UserEntity user) {
        log.info("Updating approval: {}", approvalId);

        Approval approval =
                approvalRepository.findById(approvalId).orElseThrow(() -> new ApprovalNotFoundException(approvalId));

        approval.setStatus(ModerationStatus.valueOf(request.getStatus()));
        approval.setComments(request.getComments());

        return approvalRepository.save(approval);
    }

    @Transactional(readOnly = true)
    public Approval getApprovalById(UUID approvalId) {
        return approvalRepository.findById(approvalId).orElseThrow(() -> new ApprovalNotFoundException(approvalId));
    }
}
