/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (C) 2025 RootBytes. All Rights Reserved.
 * Author: Phani K
 */

package com.github.phanikb.rootbytes.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.phanikb.rootbytes.dto.response.InvitationCodeResponse;
import com.github.phanikb.rootbytes.dto.response.RbApiResponse;
import com.github.phanikb.rootbytes.entity.InvitationCode;
import com.github.phanikb.rootbytes.entity.UserEntity;
import com.github.phanikb.rootbytes.service.InvitationCodeService;

@Slf4j
@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationCodeService invitationCodeService;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RbApiResponse<InvitationCodeResponse>> generateInvitationCode(
            @AuthenticationPrincipal UserEntity admin, @RequestBody GenerateInvitationRequest request) {
        log.info("Admin {} is generating invitation code for email: {}", admin.getEmail(), request.getInviteeEmail());

        InvitationCode invitation = invitationCodeService.generateInvitationCode(admin, request.getInviteeEmail());

        InvitationCodeResponse response = InvitationCodeResponse.builder()
                .code(invitation.getCode())
                .inviterEmail(admin.getEmail())
                .isActive(invitation.getIsActive())
                .expiresAt(invitation.getExpiresAt())
                .createdAt(invitation.getCreatedAt())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RbApiResponse.success("Invitation code generated successfully", response));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerateInvitationRequest {
        @NotBlank(message = "Invitee email is required")
        @Email(message = "Valid email is required")
        private String inviteeEmail;
    }
}
