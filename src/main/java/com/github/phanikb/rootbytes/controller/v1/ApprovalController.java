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

import com.github.phanikb.rootbytes.dto.v1.request.ApprovalRequest;
import com.github.phanikb.rootbytes.dto.v1.response.ApprovalResponse;
import com.github.phanikb.rootbytes.dto.v1.response.RbApiResponse;
import com.github.phanikb.rootbytes.entity.Approval;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.mapper.ApprovalMapper;
import com.github.phanikb.rootbytes.service.ApprovalService;

@Slf4j
@RestController
@RequestMapping("/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;
    private final ApprovalMapper approvalMapper;

    @PostMapping("/recipe/{recipeId}")
    public ResponseEntity<RbApiResponse<ApprovalResponse>> submitApproval(
            @PathVariable UUID recipeId,
            @Valid @RequestBody ApprovalRequest request,
            @AuthenticationPrincipal UserEntity user) {
        log.info("Submitting approval for recipe: {}", recipeId);

        Approval approval = approvalService.submitApproval(recipeId, request, user);
        ApprovalResponse response = approvalMapper.toResponse(approval);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RbApiResponse.success("Approval submitted successfully", response));
    }

    @PutMapping("/{approvalId}")
    public ResponseEntity<RbApiResponse<ApprovalResponse>> updateApproval(
            @PathVariable UUID approvalId,
            @Valid @RequestBody ApprovalRequest request,
            @AuthenticationPrincipal UserEntity user) {
        log.info("Updating approval: {}", approvalId);

        Approval approval = approvalService.updateApproval(approvalId, request, user);
        ApprovalResponse response = approvalMapper.toResponse(approval);

        return ResponseEntity.ok(RbApiResponse.success("Approval updated successfully", response));
    }

    @GetMapping("/{approvalId}")
    public ResponseEntity<RbApiResponse<ApprovalResponse>> getApproval(@PathVariable UUID approvalId) {
        log.debug("Fetching approval: {}", approvalId);

        Approval approval = approvalService.getApprovalById(approvalId);
        ApprovalResponse response = approvalMapper.toResponse(approval);

        return ResponseEntity.ok(RbApiResponse.success("Approval retrieved successfully", response));
    }
}
