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

import com.github.phanikb.rootbytes.dto.v1.request.ApprovalRequest;
import com.github.phanikb.rootbytes.dto.v1.response.ApprovalResponse;
import com.github.phanikb.rootbytes.entity.Approval;
import com.github.phanikb.rootbytes.entity.Recipe;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.enums.ModerationStatus;
import com.github.phanikb.rootbytes.mapper.ApprovalMapper;
import com.github.phanikb.rootbytes.service.ApprovalService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApprovalControllerTest {

    @Mock
    private ApprovalService approvalService;

    @Mock
    private ApprovalMapper approvalMapper;

    private ApprovalController controller;
    private UUID approvalId;
    private UUID recipeId;
    private UserEntity user;
    private Approval approval;
    private ApprovalResponse response;

    @BeforeEach
    void setUp() {
        controller = new ApprovalController(approvalService, approvalMapper);
        approvalId = UUID.randomUUID();
        recipeId = UUID.randomUUID();

        user = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("approver@example.com")
                .lastName("Approver")
                .uniqueName("APPROVER01")
                .build();

        Recipe recipe = Recipe.builder().id(recipeId).title("Test Recipe").build();

        approval = Approval.builder()
                .id(approvalId)
                .recipe(recipe)
                .approver(user)
                .status(ModerationStatus.APPROVED)
                .comments("Looks good")
                .approvedAt(Instant.now())
                .build();

        response = ApprovalResponse.builder()
                .id(approvalId)
                .recipeId(recipeId)
                .approverId(user.getId())
                .approverName("Approver")
                .status("APPROVED")
                .comments("Looks good")
                .approvedAt(Instant.now())
                .build();
    }

    @Test
    void shouldSubmitApproval() {
        var request = ApprovalRequest.builder()
                .status("APPROVED")
                .comments("Approved")
                .build();

        when(approvalService.submitApproval(eq(recipeId), any(ApprovalRequest.class), eq(user)))
                .thenReturn(approval);
        when(approvalMapper.toResponse(approval)).thenReturn(response);

        var result = controller.submitApproval(recipeId, request, user);

        assertEquals(201, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(response, result.getBody().getData());
    }

    @Test
    void shouldUpdateApproval() {
        var request = ApprovalRequest.builder()
                .status("REJECTED")
                .comments("Needs changes")
                .build();

        when(approvalService.updateApproval(eq(approvalId), any(ApprovalRequest.class), eq(user)))
                .thenReturn(approval);
        when(approvalMapper.toResponse(approval)).thenReturn(response);

        var result = controller.updateApproval(approvalId, request, user);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(response, result.getBody().getData());
    }

    @Test
    void shouldGetApproval() {
        when(approvalService.getApprovalById(approvalId)).thenReturn(approval);
        when(approvalMapper.toResponse(approval)).thenReturn(response);

        var result = controller.getApproval(approvalId);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getSuccess());
        assertEquals(response, result.getBody().getData());
    }
}
